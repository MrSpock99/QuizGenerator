package apps.robot.quizgenerator.createquiz.openquestion

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apps.robot.quizgenerator.data.UploadManager
import apps.robot.quizgenerator.domain.OpenQuestion
import apps.robot.quizgenerator.domain.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CreateOpenQuestionViewModel(
    private val repository: QuizRepository,
    private val uploadManager: UploadManager
) : ViewModel() {

    var state: MutableStateFlow<QuestionUiModel> =
        MutableStateFlow(
            QuestionUiModel(
                quizId = "", questionText = "", answers = emptyList(),
                currentAnswer = "", isUpdatingQuestion = false, points = 0
            )
        )
        private set

    private var questionId: String? = null
    private var questionModel: OpenQuestion? = null

    fun onReceiveArgs(id: String, questionId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val quiz = repository.getQuizModel(id)
            this@CreateOpenQuestionViewModel.questionId = questionId

            val question = quiz.list.find { it?.id == questionId } as? OpenQuestion
            questionModel = question

            state.value = state.value.copy(
                quizId = id,
                questionText = question?.text.orEmpty(),
                answers = question?.answer.orEmpty(),
                isUpdatingQuestion = question != null,
                duration = question?.duration ?: 30,
                points = question?.points ?: 1,
                answerImage = question?.answerImage?.toUri(),
                questionImage = question?.image?.toUri()
            )
        }

    }

    fun onQuestionTextChange(text: String) {
        state.value = state.value.copy(questionText = text)
    }

    fun onQuestionAnswerAdd(text: String) {
        val old = state.value.answers.toMutableList()
        old.add(text.trim().lowercase())
        state.value = state.value.copy(answers = old)
    }

    fun onQuestionAnswerChange(text: String) {
        state.value = state.value.copy(currentAnswer = text)
    }

    fun onQuestionPointsChange(text: String) {
        val points = runCatching {
           text.trim().toInt()
        }.getOrDefault(0)

        state.value = state.value.copy(points = points)
    }

    fun onCreateQuestionClick(onDone: () -> Unit) {
        viewModelScope.launch {
            val questionImage = state.value.questionImage

            val questionImagePath = if (questionImage != null) {
                uploadManager.uploadFileFromUri(
                    quizId = state.value.quizId!!,
                    fileType = "image",
                    questionId = "question_${state.value.questionText.substring(10)}",
                    uri = questionImage,
                    type = "question_image",
                )
            } else {
                null
            }

            val answerImage = state.value.answerImage

            val answerImagePath = if (answerImage != null) {
                uploadManager.uploadFileFromUri(
                    quizId = state.value.quizId!!,
                    fileType = "image",
                    questionId = "question_${state.value.questionText.substring(10)}",
                    uri = answerImage,
                    type = "answer_image",
                )
            } else {
                null
            }

            val quizId = state.value.quizId
            if (state.value.isUpdatingQuestion) {
                val model = questionModel?.copy(
                    text = state.value.questionText.trim(),
                    answer = state.value.answers,
                    duration = state.value.duration,
                    points = state.value.points,
                    image =  questionImagePath,
                    answerImage = answerImagePath
                )!!
                val job = viewModelScope.launch(Dispatchers.IO) {
                    repository.updateQuestion(quizId, model)
                }
                job.join()
                onDone()
            } else {
                val model = OpenQuestion(
                    id = UUID.randomUUID().toString(),
                    text = state.value.questionText.trim(),
                    answer = state.value.answers,
                    voiceover = null,
                    points = state.value.points,
                    duration = state.value.duration,
                    image = questionImagePath,
                    answerImage = answerImagePath
                )
                val job = viewModelScope.launch(Dispatchers.IO) {
                    repository.addQuestion(quizId, model)
                }
                job.join()
                onDone()
            }
        }
    }

    fun onDeleteAnswerClick(answer: String) {
        val old = state.value.answers.toMutableList()
        old.remove(answer)
        state.value = state.value.copy(answers = old)
    }

    fun onQuestionDurationChange(text: String) {
        val duration = runCatching {
            text.trim().toInt()
        }.getOrDefault(0)

        state.value = state.value.copy(duration = duration)
    }

    fun onQuestionImageSelected(uri: Uri) {
        state.value = state.value.copy(
            questionImage = uri
        )
    }

    fun onAnswerImageSelected(uri: Uri) {
        state.value = state.value.copy(
            answerImage = uri
        )
    }

    data class QuestionUiModel(
        val quizId: String,
        val questionText: String,
        val answers: List<String>,
        val currentAnswer: String,
        val isUpdatingQuestion: Boolean,
        val points: Int,
        val duration: Int = 30,
        val questionImage: Uri? = null,
        val answerImage: Uri? = null
    )
}