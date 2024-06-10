package apps.robot.quizgenerator.createquiz.main.presentation

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apps.robot.quizgenerator.domain.QuestionModel
import apps.robot.quizgenerator.domain.QuizModel
import apps.robot.quizgenerator.domain.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class QuizInfoViewModel(
    private val repository: QuizRepository,
    private val context: Application
): ViewModel() {

    var state = MutableStateFlow(QuizInfoUiModel("", "", emptyList()))
    private var quizModel: QuizModel? = null

    fun onNameChanged(name: String) {
        state.value = state.value.copy(name = name)
    }

    fun onSaveBtnClick(onDone: () -> Unit) {
        viewModelScope.launch {
            quizModel?.let {
                repository.saveQuiz(it.copy(name = state.value.name))
                onDone()
            }
        }
    }

    fun onReceiveArgs(quizId: String?) {
        viewModelScope.launch {
            val quizModel = if (quizId == null && quizModel == null) {
                repository.createQuizModel()
            } else {
                repository.getQuizModel(quizId ?: quizModel!!.id)
            }
            this@QuizInfoViewModel.quizModel = quizModel

            state.value =
                state.value.copy(quizId = quizModel.id, name = quizModel.name, list = quizModel.list.filterNotNull())
        }
    }

    fun onExportBtnClick(onDone: () -> Unit) {
        viewModelScope.launch {
            val json = repository.exportQuiz(quizModel!!.id)
            val clipData = ClipData.newPlainText("quiz", json)

            (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clipData)
            onDone()
        }
    }

    fun onResume() {
        viewModelScope.launch {
            repository.getQuizModel(state.value.quizId).let {
                this@QuizInfoViewModel.quizModel = it
                state.value = state.value.copy(list = it.list.filterNotNull())
            }
        }
    }

    data class QuizInfoUiModel(val quizId: String, val name: String, val list: List<QuestionModel>)
}