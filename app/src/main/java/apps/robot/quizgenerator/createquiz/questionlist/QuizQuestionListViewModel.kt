package apps.robot.quizgenerator.createquiz.questionlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apps.robot.quizgenerator.domain.QuestionModel
import apps.robot.quizgenerator.domain.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class QuizQuestionListViewModel(
    private val repository: QuizRepository
) : ViewModel() {

    var state = MutableStateFlow(QuizQuestionListUiModel("",emptyList()))

    fun onReceiveArgs(id: String?) {
        viewModelScope.launch {
            val quizModel = if (id == null) {
                repository.createQuizModel()
            } else {
                repository.getQuizModel(id)
            }
            state.value = state.value.copy(quizId = quizModel.id, list = quizModel.list.filterNotNull())
        }
    }

    fun onQuestionClick(questionModel: QuestionModel) {

    }

    fun onAddQuestionClick() {

    }

    data class QuizQuestionListUiModel(val quizId: String, val list: List<QuestionModel>)
}