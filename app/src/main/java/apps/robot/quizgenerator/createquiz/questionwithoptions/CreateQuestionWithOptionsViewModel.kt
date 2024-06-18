package apps.robot.quizgenerator.createquiz.questionwithoptions

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apps.robot.quizgenerator.data.UploadManager
import apps.robot.quizgenerator.domain.QuestionWithOptions
import apps.robot.quizgenerator.domain.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CreateQuestionWithOptionsViewModel(
    private val repository: QuizRepository,
    private val uploadManager: UploadManager
) : ViewModel() {

    private val defaultOptions = listOf(
        "",
        "",
        "",
        ""
    )
    val state: MutableStateFlow<CreateQuestionsWithOptionsUiModel> = MutableStateFlow(
        CreateQuestionsWithOptionsUiModel(
            "",
            defaultOptions,
            false,
            -1,
            points = 0
        )
    )

    private var quizId: String? = null
    private var questionModel: QuestionWithOptions? = null

    fun onReceiveArgs(quizId: String, questionId: String?) {
        this.quizId = quizId

        viewModelScope.launch(Dispatchers.IO) {
            val quizModel = repository.getQuizModel(quizId)
            val question = quizModel.list.find { it?.id == questionId } as? QuestionWithOptions
            this@CreateQuestionWithOptionsViewModel.questionModel = question

            state.value = state.value.copy(
                text = question?.text.orEmpty(),
                answers = question?.options ?: defaultOptions,
                isUpdatingQuestion = question != null,
                rightAnswerIndex = question?.rightAnswerIndex ?: -1,
                duration = question?.duration ?: 30,
                points = question?.points ?: 1,
                questionImage = question?.image?.toUri(),
                answerImage = question?.answerImage?.toUri()
            )
        }
    }

    fun onQuestionTextChange(text: String) {
        state.value = state.value.copy(text = text)
    }

    fun onQuestionPointsChange(text: String) {
        val points = runCatching {
            text.trim().toInt()
        }.getOrDefault(0)

        state.value = state.value.copy(points = points)
    }

    fun onQuestionDurationChange(text: String) {
        val duration = runCatching {
            text.trim().toInt()
        }.getOrDefault(0)

        state.value = state.value.copy(duration = duration)
    }

    fun onCreateQuestionClick(onDone: () -> Unit) {
        viewModelScope.launch {
            val questionImage = state.value.questionImage

            val questionImagePath = if (questionImage != null) {
                uploadManager.uploadFileFromUri(
                    quizId = quizId!!,
                    fileType = "image",
                    questionId = "question_${state.value.text}",
                    uri = questionImage,
                    type = "question_image",
                )
            } else {
                null
            }

            val answerImage = state.value.answerImage

            val answerImagePath = if (answerImage != null) {
                uploadManager.uploadFileFromUri(
                    quizId = quizId!!,
                    fileType = "image",
                    questionId = "question_${state.value.text}",
                    uri = answerImage,
                    type = "answer_image",
                )
            } else {
                null
            }

            if (state.value.isUpdatingQuestion) {
                val model = questionModel?.copy(
                    text = state.value.text.trim(),
                    options = state.value.answers,
                    rightAnswerIndex = state.value.rightAnswerIndex,
                    duration = state.value.duration,
                    image = questionImagePath,
                    answerImage = answerImagePath
                )!!
                val job = launch(Dispatchers.IO) {
                    repository.updateQuestion(quizId!!, model)
                }
                job.join()
                onDone()
            } else {
                val model = QuestionWithOptions(
                    id = UUID.randomUUID().toString(),
                    text = state.value.text.trim(),
                    options = state.value.answers,
                    rightAnswerIndex = state.value.rightAnswerIndex,
                    image = questionImagePath,
                    voiceover = null,
                    points = state.value.points,
                    duration = state.value.duration,
                    answerImage = answerImagePath
                )
                val job = launch(Dispatchers.IO) {
                    repository.addQuestion(quizId!!, model)
                }
                job.join()
                onDone()
            }
        }
    }

    fun onAnswerChecked(index: Int) {
        state.value = state.value.copy(rightAnswerIndex = index)
    }

    fun onAnswerTextChange(text: String, index: Int) {
        val oldList = state.value.answers.toMutableList()
        oldList[index] = text
        state.value = state.value.copy(
            answers = oldList
        )
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

    data class CreateQuestionsWithOptionsUiModel(
        val text: String,
        val answers: List<String>,
        val isUpdatingQuestion: Boolean,
        val rightAnswerIndex: Int,
        val points: Int,
        val duration: Int = 30,
        val questionImage: Uri? = null,
        val answerImage: Uri? = null
    )
}