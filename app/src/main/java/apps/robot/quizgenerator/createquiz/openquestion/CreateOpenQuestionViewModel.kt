package apps.robot.quizgenerator.createquiz.openquestion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apps.robot.quizgenerator.domain.OpenQuestion
import apps.robot.quizgenerator.domain.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CreateOpenQuestionViewModel(
    private val repository: QuizRepository
) : ViewModel() {

    var state: MutableStateFlow<QuestionUiModel> =
        MutableStateFlow(
            QuestionUiModel(
                quizId = "", questionText = "", answers = emptyList(),
                currentAnswer = "", isUpdatingQuestion = false, points = 0
            )
        )
        private set

    private var questionId: String? = null
    private var questionModel: OpenQuestion? = null

    fun onReceiveArgs(id: String, questionId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val quiz = repository.getQuizModel(id)
            this@CreateOpenQuestionViewModel.questionId = questionId

            val question = quiz.list.find { it?.id == questionId } as? OpenQuestion
            questionModel = question

            state.value = state.value.copy(
                quizId = id,
                questionText = question?.text.orEmpty(),
                answers = question?.answer.orEmpty(),
                isUpdatingQuestion = question != null
            )
        }

    }

    fun onQuestionTextChange(text: String) {
        state.value = state.value.copy(questionText = text)
    }

    fun onQuestionAnswerAdd(text: String) {
        val old = state.value.answers.toMutableList()
        old.add(text.trim().lowercase())
        state.value = state.value.copy(answers = old)
    }

    fun onQuestionAnswerChange(text: String) {
        state.value = state.value.copy(currentAnswer = text)
    }

    fun onQuestionPointsChange(text: String) {
        val points = runCatching {
           text.trim().toInt()
        }.getOrDefault(0)

        state.value = state.value.copy(points = points)
    }

    fun onCreateQuestionClick(onDone: () -> Unit) {
        viewModelScope.launch {
            val quizId = state.value.quizId
            if (state.value.isUpdatingQuestion) {
                val model = questionModel?.copy(
                    text = state.value.questionText.trim(),
                    answer = state.value.answers
                )!!
                val job = viewModelScope.launch(Dispatchers.IO) {
                    repository.updateQuestion(quizId, model)
                }
                job.join()
                onDone()
            } else {
                val model = OpenQuestion(
                    id = UUID.randomUUID().toString(),
                    text = state.value.questionText.trim(),
                    answer = state.value.answers,
                    image = null,
                    voiceover = null,
                    type = "OpenQuestion",
                    points = state.value.points
                )
                val job = viewModelScope.launch(Dispatchers.IO) {
                    repository.addQuestion(quizId, model)
                }
                job.join()
                onDone()
            }
        }
    }

    fun onDeleteAnswerClick(answer: String) {
        val old = state.value.answers.toMutableList()
        old.remove(answer)
        state.value = state.value.copy(answers = old)
    }

    data class QuestionUiModel(
        val quizId: String,
        val questionText: String,
        val answers: List<String>,
        val currentAnswer: String,
        val isUpdatingQuestion: Boolean,
        val points: Int
    )
}