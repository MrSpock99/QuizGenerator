package apps.robot.quizgenerator.createquiz.base

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apps.robot.quizgenerator.domain.AudioUploadDelegate
import apps.robot.quizgenerator.domain.ImageUploadDelegate
import apps.robot.quizgenerator.domain.QuestionModel
import apps.robot.quizgenerator.domain.QuizRepository
import apps.robot.quizgenerator.utils.AudioPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class CreateQuestionViewModel(
    private val repository: QuizRepository,
    private val imageUploadDelegate: ImageUploadDelegate,
    private val audioUploadDelegate: AudioUploadDelegate,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private val defaultOptions = listOf("", "", "", "")
    protected val _state: MutableStateFlow<QuestionUiState> = MutableStateFlow(QuestionUiState.Loading)
    val state: StateFlow<QuestionUiState> = _state.asStateFlow()

    protected var quizId: String? = null
    protected var questionModel: QuestionModel? = null
    protected var questionId: String? = null

    abstract fun onReceiveArgs(id: String, questionId: String?)

    fun onQuestionTextChange(text: String) {
        val currentState = _state.value
        if (currentState is QuestionUiState.EditableQuestionUiState) {
            _state.value = currentState.copyEditable(questionText = text)
        }
    }

    fun onQuestionPointsChange(text: String) {
        val points = runCatching { text.trim() }.getOrDefault("")
        val currentState = _state.value
        if (currentState is QuestionUiState.EditableQuestionUiState) {
            _state.value = currentState.copyEditable(points = points)
        }
    }

    fun onQuestionDurationChange(text: String) {
        val duration = runCatching { text.trim() }.getOrDefault("")
        val currentState = _state.value
        if (currentState is QuestionUiState.EditableQuestionUiState) {
            _state.value = currentState.copyEditable(duration = duration)
        }
    }

    abstract fun onCreateQuestionClick(onDone: () -> Unit)

    fun onQuestionImageSelected(uri: Uri) {
        val currentState = _state.value
        if (currentState is QuestionUiState.EditableQuestionUiState) {
            _state.value = currentState.copyEditable(questionImage = uri)
        }
    }

    fun onAnswerImageSelected(uri: Uri?) {
        val currentState = _state.value
        if (currentState is QuestionUiState.EditableQuestionUiState) {
            _state.value = currentState.copyEditable(answerImage = uri)
        }
    }

    fun onPlayAudioClicked(uri: Uri) {
        val currentState = _state.value
        if (currentState is QuestionUiState.EditableQuestionUiState) {
            _state.value = currentState.playAudio(uri)
        }
        viewModelScope.launch { audioPlayer.playAudio(uri) }
    }

    fun onStopAudioClicked() {
        val currentState = _state.value
        if (currentState is QuestionUiState.EditableQuestionUiState) {
            _state.value = currentState.stopAudio()
        }
        audioPlayer.stopAudio()
    }

    fun onAnswerAudioSelected(uri: Uri) {
        val currentState = _state.value
        if (currentState is QuestionUiState.EditableQuestionUiState) {
            _state.value = currentState.copyEditable(
                answerAudio = uri
            )
        }
    }

    fun onQuestionAudioSelected(uri: Uri) {
        val currentState = _state.value
        if (currentState is QuestionUiState.EditableQuestionUiState) {
            _state.value = currentState.copyEditable(
                questionAudio = uri
            )
        }
    }

    sealed class QuestionUiState {
        data object Loading : QuestionUiState()

        sealed class EditableQuestionUiState(
            open val questionText: String,
            open val points: String,
            open val duration: String,
            open val isUpdatingQuestion: Boolean,
            open val questionImage: Uri?,
            open val answerImage: Uri?,
            open val answerAudio: Uri?,
            open val questionAudio: Uri?,
            open val playAudioState: AudioState,
            open val questionVideo: Uri?,
            open val answerVideo: Uri?
        ) : QuestionUiState() {

            private fun updateQuestionAudioImpl(uri: Uri): EditableQuestionUiState {
                return copyEditable(questionAudio = uri)
            }

            private fun updateAnswerAudioImpl(uri: Uri): EditableQuestionUiState {
                return copyEditable(answerAudio = uri)
            }

            private fun playAudioImpl(): EditableQuestionUiState {
                return copyEditable(playAudioState = AudioState.Playing)
            }

            private fun stopAudioImpl(): EditableQuestionUiState {
                return copyEditable(playAudioState = AudioState.Paused)
            }

            private fun updateQuestionTextImpl(text: String): EditableQuestionUiState {
                return copyEditable(questionText = text)
            }

            private fun updateDurationImpl(text: String): EditableQuestionUiState {
                return copyEditable(duration = text)
            }

            private fun updatePointsImpl(text: String): EditableQuestionUiState {
                return copyEditable(points = text)
            }

            abstract fun copyEditable(
                questionText: String = this.questionText,
                points: String = this.points,
                duration: String = this.duration,
                isUpdatingQuestion: Boolean = this.isUpdatingQuestion,
                questionImage: Uri? = this.questionImage,
                answerImage: Uri? = this.answerImage,
                answerAudio: Uri? = this.answerAudio,
                questionAudio: Uri? = this.questionAudio,
                playAudioState: AudioState = this.playAudioState
            ): EditableQuestionUiState

            fun updateQuestionAudio(uri: Uri) = updateQuestionAudioImpl(uri)
            fun updateAnswerAudio(uri: Uri) = updateAnswerAudioImpl(uri)
            fun playAudio(uri: Uri) = playAudioImpl()
            fun stopAudio() = stopAudioImpl()
            fun updateQuestionText(text: String) = updateQuestionTextImpl(text)
            fun updateDuration(text: String) = updateDurationImpl(text)
            fun updatePoints(text: String) = updatePointsImpl(text)
        }

        data class QuestionWithOptionsUiState(
            val rightAnswerIndex: Int,
            val answers: List<String>,
            override val questionAudio: Uri?,
            override val answerAudio: Uri?,
            override val questionText: String,
            override val playAudioState: AudioState,
            override val points: String,
            override val duration: String,
            override val isUpdatingQuestion: Boolean,
            override val questionImage: Uri?,
            override val answerImage: Uri?,
            override val questionVideo: Uri?,
            override val answerVideo: Uri?
        ) : EditableQuestionUiState(
            questionText = questionText,
            points = points,
            duration = duration,
            isUpdatingQuestion = isUpdatingQuestion,
            questionImage = questionImage,
            answerImage = answerImage,
            answerAudio = answerAudio,
            questionAudio = questionAudio,
            playAudioState = playAudioState,
            questionVideo = questionVideo,
            answerVideo = answerVideo
        ) {
            override fun copyEditable(
                questionText: String,
                points: String,
                duration: String,
                isUpdatingQuestion: Boolean,
                questionImage: Uri?,
                answerImage: Uri?,
                answerAudio: Uri?,
                questionAudio: Uri?,
                playAudioState: AudioState
            ) = QuestionWithOptionsUiState(
                rightAnswerIndex,
                questionText = questionText,
                answerAudio = answerAudio,
                questionAudio = questionAudio,
                playAudioState = playAudioState,
                points = points,
                duration = duration,
                isUpdatingQuestion = isUpdatingQuestion,
                questionImage = questionImage,
                answerImage = answerImage,
                answers = answers,
                questionVideo = null,
                answerVideo = null
            )
        }

        data class OpenQuestionUiState(
            val currentAnswer: String,
            val answers: List<String>,
            override val questionAudio: Uri?,
            override val answerAudio: Uri?,
            override val questionText: String,
            override val playAudioState: AudioState,
            override val points: String,
            override val duration: String,
            override val isUpdatingQuestion: Boolean,
            override val questionImage: Uri?,
            override val answerImage: Uri?,
            override val questionVideo: Uri?,
            override val answerVideo: Uri?
        ) : EditableQuestionUiState(
            questionText = questionText,
            points = points,
            duration = duration,
            isUpdatingQuestion = isUpdatingQuestion,
            questionImage = questionImage,
            answerImage = answerImage,
            answerAudio = answerAudio,
            questionAudio = questionAudio,
            playAudioState = playAudioState,
            answerVideo = null,
            questionVideo = null,
        ) {
            override fun copyEditable(
                questionText: String,
                points: String,
                duration: String,
                isUpdatingQuestion: Boolean,
                questionImage: Uri?,
                answerImage: Uri?,
                answerAudio: Uri?,
                questionAudio: Uri?,
                playAudioState: AudioState
            ) = OpenQuestionUiState(
                currentAnswer = currentAnswer,
                questionAudio = questionAudio,
                answerAudio = answerAudio,
                questionText = questionText,
                playAudioState = playAudioState,
                points = points,
                duration = duration,
                answerImage = answerImage,
                questionImage = questionImage,
                isUpdatingQuestion = isUpdatingQuestion,
                answers = answers,
                questionVideo = null,
                answerVideo = null
            )
        }

        enum class AudioState {
            Playing, Paused
        }
    }
}