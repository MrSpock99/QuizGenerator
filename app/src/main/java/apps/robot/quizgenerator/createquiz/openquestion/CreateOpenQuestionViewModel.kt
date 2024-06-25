package apps.robot.quizgenerator.createquiz.openquestion

import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import apps.robot.quizgenerator.createquiz.base.CreateQuestionViewModel
import apps.robot.quizgenerator.domain.AudioUploadDelegate
import apps.robot.quizgenerator.domain.ImageUploadDelegate
import apps.robot.quizgenerator.domain.OpenQuestion
import apps.robot.quizgenerator.domain.QuizRepository
import apps.robot.quizgenerator.utils.AudioPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class CreateOpenQuestionViewModel(
    private val repository: QuizRepository,
    private val imageUploadDelegate: ImageUploadDelegate,
    private val audioUploadDelegate: AudioUploadDelegate,
    private val audioPlayer: AudioPlayer
) : CreateQuestionViewModel(repository, imageUploadDelegate, audioUploadDelegate, audioPlayer) {

    override fun onReceiveArgs(id: String, questionId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val quiz = repository.getQuizModel(id)
            this@CreateOpenQuestionViewModel.questionId = questionId
            this@CreateOpenQuestionViewModel.quizId = quiz.id

            val question = quiz.list.find { it?.id == questionId } as? OpenQuestion
            questionModel = question

            _state.value = QuestionUiState.OpenQuestionUiState(
                questionText = question?.text.orEmpty(),
                answers = question?.answer.orEmpty(),
                isUpdatingQuestion = question != null,
                duration = (question?.duration ?: 30).toString(),
                points = (question?.points ?: 1).toString(),
                answerImage = (question?.answerImage)?.toUri(),
                questionImage = (question?.image)?.toUri(),
                currentAnswer = "",
                questionAudio = question?.questionAudio?.toUri(),
                questionVideo = question?.questionVideo?.toUri(),
                answerAudio = question?.answerAudio?.toUri(),
                answerVideo = question?.answerVideo?.toUri(),
                playAudioState = QuestionUiState.AudioState.Paused
            )
        }

    }

    fun onQuestionAnswerAdd(text: String) {
        val currentState = state.value
        if (currentState is QuestionUiState.OpenQuestionUiState) {
            val old = currentState.answers.toMutableList()
            old.add(text.trim().lowercase())
            _state.value = currentState.copy(answers = old)
        }

    }

    fun onQuestionAnswerChange(text: String) {
        val currentState = state.value
        if (currentState is QuestionUiState.OpenQuestionUiState) {
            _state.value = currentState.copy(currentAnswer = text)
        }
    }

    override fun onCreateQuestionClick(onDone: () -> Unit) {
        viewModelScope.launch {
            val currentState = state.value

            if (currentState is QuestionUiState.OpenQuestionUiState) {
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
                val model = OpenQuestion(
                    id = questionModel?.id ?: UUID.randomUUID().toString(),
                    text = currentState.questionText.trim(),
                    answer = currentState.answers,
                    voiceover = null,
                    points = currentState.points.trim().toInt(),
                    duration = currentState.duration.trim().toInt(),
                    image = questionImagePath,
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
                onDone()
            }
        }
    }

    fun onDeleteAnswerClick(answer: String) {
        val currentState = state.value
        if (currentState is QuestionUiState.OpenQuestionUiState) {
            val old = currentState.answers.toMutableList()
            old.remove(answer)
            _state.value = currentState.copy(answers = old)
        }
    }
}