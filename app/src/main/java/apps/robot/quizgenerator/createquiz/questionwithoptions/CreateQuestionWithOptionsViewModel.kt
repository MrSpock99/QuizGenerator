package apps.robot.quizgenerator.createquiz.questionwithoptions

import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import apps.robot.quizgenerator.createquiz.base.CreateQuestionViewModel
import apps.robot.quizgenerator.domain.AudioUploadDelegate
import apps.robot.quizgenerator.domain.ImageUploadDelegate
import apps.robot.quizgenerator.domain.QuestionWithOptions
import apps.robot.quizgenerator.domain.QuizRepository
import apps.robot.quizgenerator.utils.AudioPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class CreateQuestionWithOptionsViewModel(
    private val repository: QuizRepository,
    private val imageUploadDelegate: ImageUploadDelegate,
    private val audioUploadDelegate: AudioUploadDelegate,
    private val audioPlayer: AudioPlayer
) : CreateQuestionViewModel(repository, imageUploadDelegate, audioUploadDelegate, audioPlayer) {

    private val defaultOptions = listOf(
        "",
        "",
        "",
        ""
    )

    override fun onReceiveArgs(quizId: String, questionId: String?) {
        this.quizId = quizId

        viewModelScope.launch(Dispatchers.IO) {
            val quizModel = repository.getQuizModel(quizId)
            val question = quizModel.list.find { it?.id == questionId } as? QuestionWithOptions
            this@CreateQuestionWithOptionsViewModel.questionModel = question

            _state.value = QuestionUiState.QuestionWithOptionsUiState(
                questionText = question?.text.orEmpty(),
                answers = question?.options ?: defaultOptions,
                isUpdatingQuestion = question != null,
                rightAnswerIndex = question?.rightAnswerIndex ?: -1,
                duration = (question?.duration ?: 30).toString(),
                points = (question?.points ?: 1).toString(),
                answerImage = (question?.answerImage)?.toUri(),
                questionImage = (question?.image)?.toUri(),
                questionAudio = question?.questionAudio?.toUri(),
                questionVideo = question?.questionVideo?.toUri(),
                answerAudio = question?.answerAudio?.toUri(),
                answerVideo = question?.answerVideo?.toUri(),
                playAudioState = QuestionUiState.AudioState.Paused
            )
        }
    }


    override fun onCreateQuestionClick(onDone: () -> Unit) {
        viewModelScope.launch {
            val currentState = state.value
            _state.value = QuestionUiState.Loading
            if (currentState is QuestionUiState.QuestionWithOptionsUiState) {
                val questionImage = currentState.questionImage
                val answerImage = currentState.answerImage
                val questionAudio = currentState.questionAudio
                val answerAudio = currentState.answerAudio

                _state.value = QuestionUiState.Loading

                val imagesPaths = imageUploadDelegate.upload(
                    questionImage = questionImage,
                    answerImage = answerImage,
                    quizId = quizId!!,
                    questionText = currentState.questionText,
                )
                val audioPaths = audioUploadDelegate.upload(
                    questionAudio = questionAudio,
                    answerAudio = answerAudio,
                    quizId = quizId!!,
                    questionText = currentState.questionText
                )
                val questionImagePath = imagesPaths.questionPath
                val answerImagePath = imagesPaths.answerPath

                val quizId = quizId!!

                val model = QuestionWithOptions(
                    id = questionModel?.id ?: UUID.randomUUID().toString(),
                    text = currentState.questionText.trim(),
                    options = currentState.answers,
                    rightAnswerIndex = currentState.rightAnswerIndex,
                    image = questionImagePath,
                    voiceover = null,
                    points = currentState.points.toIntOrNull() ?: 0, // Handle potential parsing issues
                    duration = currentState.duration.toIntOrNull() ?: 0, // Handle potential parsing issues
                    answerImage = answerImagePath,
                    questionAudio = audioPaths.questionPath,
                    answerAudio = audioPaths.answerPath,
                    questionVideo = null,
                    answerVideo = null
                )
                if (questionModel == null) {
                    repository.addQuestion(quizId, model)
                } else {
                    repository.updateQuestion(quizId, model)
                }
                /*if (currentState.isUpdatingQuestion) {
                    var model = questionModel?.copy(
                        text = currentState.questionText.trim(),
                        options = currentState.answers,
                        rightAnswerIndex = currentState.rightAnswerIndex,
                        duration = currentState.duration.toIntOrNull() ?: 0, // Handle potential parsing issues
                    )!!
                    if (questionImagePath != null) {
                        model = model.copy(
                            image = questionImagePath,
                        )
                    }
                    if (answerImagePath != null) {
                        model = model.copy(
                            answerImage = answerImagePath
                        )
                    }
                    val job = launch(Dispatchers.IO) {
                        repository.updateQuestion(quizId!!, model)
                    }
                    job.join()
                    onDone()
                } else {
                    val model = QuestionWithOptions(
                        id = UUID.randomUUID().toString(),
                        text = currentState.questionText.trim(),
                        options = currentState.answers,
                        rightAnswerIndex = currentState.rightAnswerIndex,
                        image = questionImagePath,
                        voiceover = null,
                        points = currentState.points.toIntOrNull() ?: 0, // Handle potential parsing issues
                        duration = currentState.duration.toIntOrNull() ?: 0, // Handle potential parsing issues
                        answerImage = answerImagePath,
                        questionAudio = audioPaths.questionPath,
                        answerAudio = audioPaths.answerPath,
                        questionVideo = null,
                        answerVideo = null
                    )
                    val job = launch(Dispatchers.IO) {
                        repository.addQuestion(quizId!!, model)
                    }
                    job.join()
                    onDone()
                }*/
            }
        }
    }

    fun onAnswerChecked(index: Int) {
        val currentState = state.value
        if (currentState is QuestionUiState.QuestionWithOptionsUiState) {
            _state.value = currentState.copy(rightAnswerIndex = index)
        }
    }

    fun onAnswerTextChange(text: String, index: Int) {
        val currentState = state.value
        if (currentState is QuestionUiState.QuestionWithOptionsUiState) {
            val oldList = currentState.answers.toMutableList()
            oldList[index] = text
            _state.value = currentState.copy(answers = oldList)
        }
    }
}