package apps.robot.quizgenerator.createquiz.questionwithoptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apps.robot.quizgenerator.domain.QuestionWithOptions
import apps.robot.quizgenerator.domain.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CreateQuestionWithOptionsViewModel(
    private val repository: QuizRepository
) : ViewModel() {

    private val defaultOptions = listOf(
        "",
        "",
        "",
        ""
    )
    val state: MutableStateFlow<CreateQuestionsWithOptionsUiModel> = MutableStateFlow(
        CreateQuestionsWithOptionsUiModel(
            "",
            defaultOptions,
            false,
            -1
        )
    )

    private var quizId: String? = null
    private var questionModel: QuestionWithOptions? = null

    fun onReceiveArgs(quizId: String, questionId: String?) {
        this.quizId = quizId

        viewModelScope.launch(Dispatchers.IO) {
            val quizModel = repository.getQuizModel(quizId)
            val question = quizModel.list.find { it?.id == questionId } as? QuestionWithOptions
            this@CreateQuestionWithOptionsViewModel.questionModel = question

            state.value = state.value.copy(
                text = question?.text.orEmpty(),
                answers = question?.options ?: defaultOptions,
                isUpdatingQuestion = question != null,
                rightAnswerIndex = question?.rightAnswerIndex ?: -1
            )
        }
    }

    fun onQuestionTextChange(text: String) {
        state.value = state.value.copy(text = text)
    }

    fun onCreateQuestionClick(onDone: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isUpdatingQuestion) {
                val model = questionModel?.copy(
                    text = state.value.text,
                    options = state.value.answers,
                    rightAnswerIndex = state.value.rightAnswerIndex
                )!!
                val job = viewModelScope.launch(Dispatchers.IO) {
                    repository.updateQuestion(quizId!!, model)
                }
                job.join()
                onDone()
            } else {
                val model = QuestionWithOptions(
                    id = UUID.randomUUID().toString(),
                    text = state.value.text,
                    options = state.value.answers,
                    rightAnswerIndex = state.value.rightAnswerIndex,
                    image = null,
                    voiceover = null
                )
                val job = viewModelScope.launch(Dispatchers.IO) {
                    repository.addQuestion(quizId!!, model)
                }
                job.join()
                onDone()
            }
        }
    }

    fun onAnswerChecked(index: Int) {
        state.value = state.value.copy(rightAnswerIndex = index)
    }

    fun onAnswerTextChange(text: String, index: Int) {
        val oldList = state.value.answers.toMutableList()
        oldList[index] = text
        state.value = state.value.copy(
            answers = oldList
        )
    }

    data class CreateQuestionsWithOptionsUiModel(
        val text: String,
        val answers: List<String>,
        val isUpdatingQuestion: Boolean,
        val rightAnswerIndex: Int
    )
}