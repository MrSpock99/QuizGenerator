package apps.robot.quizgenerator.createquiz.presentation

import androidx.lifecycle.ViewModel
import apps.robot.quizgenerator.domain.QuizRepository
import apps.robot.quizgenerator.quizlist.presentation.QuizListViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class CreateQuizViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {

    var state: MutableStateFlow<QuizListViewModel.QuizListUiState> =
        MutableStateFlow(QuizListViewModel.QuizListUiState.Loading)
        private set

    init {

    }


    sealed interface CreateQuizUiModel {
        object Loading : CreateQuizUiModel
        data class Success(val quizId: String) : CreateQuizUiModel
        data class Error(val message: String) : CreateQuizUiModel
    }
}