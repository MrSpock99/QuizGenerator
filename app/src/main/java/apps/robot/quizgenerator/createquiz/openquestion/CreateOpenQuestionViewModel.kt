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
                quizId = "", questionName = "", questionText = "", answers = emptyList(),
                currentAnswer = "", isUpdatingQuestion = false
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
                questionName = question?.title.orEmpty(),
                questionText = question?.text.orEmpty(),
                answers = question?.answer.orEmpty(),
                isUpdatingQuestion = question != null
            )
        }

    }

    fun onQuestionNameChange(name: String) {
        state.value = state.value.copy(questionName = name)
    }

    fun onQuestionTextChange(text: String) {
        state.value = state.value.copy(questionText = text)
    }

    fun onQuestionAnswerAdd(text: String) {
        val old = state.value.answers.toMutableList()
        old.add(text)
        state.value = state.value.copy(answers = old)
    }

    fun onQuestionAnswerChange(text: String) {
        state.value = state.value.copy(currentAnswer = text)
    }

    fun onCreateQuestionClick(onDone: () -> Unit) {
        viewModelScope.launch {
            val quizId = state.value.quizId
            if (state.value.isUpdatingQuestion) {
                val model = questionModel?.copy(
                    title = state.value.questionName,
                    text = state.value.questionText,
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
                    title = state.value.questionName,
                    text = state.value.questionText,
                    answer = state.value.answers,
                    image = null,
                    voiceover = null
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
        val questionName: String,
        val questionText: String,
        val answers: List<String>,
        val currentAnswer: String,
        val isUpdatingQuestion: Boolean
    )
}