package apps.robot.quizgenerator.quizlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apps.robot.quizgenerator.domain.QuizModel
import apps.robot.quizgenerator.domain.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class QuizListViewModel(
    private val repository: QuizRepository
): ViewModel() {

    var state: MutableStateFlow<QuizListUiState> = MutableStateFlow(QuizListUiState.Loading)
        private set

    fun onResume() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getQuizList()

            state.emit(QuizListUiState.Success(list as List<QuizModel>))
        }
    }
    sealed class QuizListUiState {

        object Loading: QuizListUiState()
        class Success(val quizList: List<QuizModel>): QuizListUiState()
        class Error(e: Throwable): QuizListUiState()
    }
}