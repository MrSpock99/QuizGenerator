package apps.robot.quizgenerator.createquiz.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apps.robot.quizgenerator.domain.QuizModel
import apps.robot.quizgenerator.domain.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class QuizInfoViewModel(
    private val repository: QuizRepository
): ViewModel() {

    var state = MutableStateFlow(QuizInfoUiModel(""))
    private var quizModel: QuizModel? = null

    fun onNameChanged(name: String) {
        state.value = state.value.copy(name = name)
    }

    fun onSaveBtnClick() {
        viewModelScope.launch {
            quizModel?.let { repository.saveQuiz(it.copy(name = state.value.name)) }
        }
    }

    fun onReceiveArgs(quizId: String?) {
        if (quizId == null) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val quizModel = repository.getQuizModel(quizId)
            state.value = state.value.copy(name = quizModel.name)
            this@QuizInfoViewModel.quizModel = quizModel
        }
    }

    data class QuizInfoUiModel(val name: String)
}