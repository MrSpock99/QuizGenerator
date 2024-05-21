package apps.robot.quizgenerator.createquiz.presentation

import androidx.lifecycle.ViewModel
import apps.robot.quizgenerator.domain.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow

class CreateOpenQuestionViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {

    var state: MutableStateFlow<QuestionModel> =
        MutableStateFlow(QuestionModel(quizId = "", questionName = "", questionText = ""))
        private set

    init {

    }

    fun onQuestionNameChange(name: String) {
        state.value = state.value.copy(questionName = name)
    }

    fun onQuestionTextChange(text: String) {
        state.value = state.value.copy(questionText = text)
    }

    fun onCreateQuestionClick() {

    }


    data class QuestionModel(val quizId: String, val questionName: String, val questionText: String) : CreateOpenQuestionUiModel

    sealed interface CreateOpenQuestionUiModel {
        object Loading : CreateOpenQuestionUiModel
        data class QuestionModel(val quizId: String, val questionName: String, val questionText: String) : CreateOpenQuestionUiModel
        data class Error(val message: String) : CreateOpenQuestionUiModel
    }
}