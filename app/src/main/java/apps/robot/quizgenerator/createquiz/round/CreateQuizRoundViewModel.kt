package apps.robot.quizgenerator.createquiz.round

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apps.robot.quizgenerator.domain.ImageUploadDelegate
import apps.robot.quizgenerator.domain.QuestionModel
import apps.robot.quizgenerator.domain.QuizRepository
import apps.robot.quizgenerator.domain.QuizRound
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CreateQuizRoundViewModel(
    private val repository: QuizRepository,
    private val imageUploadDelegate: ImageUploadDelegate
) : ViewModel() {

    var uiState = MutableStateFlow(UiState())
    private var quizId: String? = null
    private var roundModel: QuestionModel? = null

    fun onReceiveArgs(quizId: String, roundId: String?) {
        this.quizId = quizId
        viewModelScope.launch {
            val quizModel = repository.getQuizModel(quizId)
            val question = quizModel.list.find { it?.id == roundId } as? QuizRound
            this@CreateQuizRoundViewModel.roundModel = question
            uiState.value = UiState(
                title = question?.title.orEmpty(),
                text = question?.text.orEmpty(),
                image = question?.image?.toUri()
            )
        }
    }

    fun onTitleChange(title: String) {
        uiState.value = uiState.value.copy(title = title)
    }

    fun onTextChange(text: String) {
        uiState.value = uiState.value.copy(text = text)
    }

    fun onCreateClick(onDone: () -> Unit) {
        viewModelScope.launch {
            val model = QuizRound(
                id = roundModel?.id ?: UUID.randomUUID().toString(),
                title = uiState.value.title,
                text = uiState.value.text,
                image = if (uiState.value.image != null) {
                    uiState.value.image?.toString()
                } else {
                    roundModel?.image
                }
            )
            if (roundModel == null) {
                repository.addQuestion(quizId!!, model)
            } else {
                repository.updateQuestion(quizId!!, model)
            }
            onDone()
        }
    }

    fun onImageSelected(uri: Uri) {
        uiState.value = uiState.value.copy(
            image = uri
        )
    }

    data class UiState(
        val title: String = "",
        val text: String = "",
        val image: Uri? = null
    )
}