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
        MutableStateFlow(QuestionUiModel(quizId = "", questionName = "", questionText = "", emptyList()))
        private set

    fun onQuestionNameChange(name: String) {
        state.value = state.value.copy(questionName = name)
    }

    fun onQuestionTextChange(text: String) {
        state.value = state.value.copy(questionText = text)
    }
    fun onQuestionAnswerChange(text: String) {
        val old = state.value.answer.toMutableList()
        old.add(text)
        state.value = state.value.copy(answer = old)
    }

    fun onCreateQuestionClick() {
        val quizId = state.value.quizId
        val model = OpenQuestion(
            id = UUID.randomUUID().toString(),
            title = state.value.questionName,
            text = state.value.questionText,
            answer = state.value.answer,
            image = null,
            voiceover = null
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.addQuestion(quizId, model)
        }
    }

    fun onReceiveArgs(id: String) {
        state.value = state.value.copy(quizId = id)
    }

    data class QuestionUiModel(val quizId: String, val questionName: String, val questionText: String, val answer: List<String>) :
        CreateOpenQuestionUiModel

    sealed interface CreateOpenQuestionUiModel {
        object Loading : CreateOpenQuestionUiModel
        data class QuestionModel(val quizId: String, val questionName: String, val questionText: String) :
            CreateOpenQuestionUiModel
        data class Error(val message: String) : CreateOpenQuestionUiModel
    }
}