package apps.robot.quizgenerator.createquiz.questionwithoptions

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apps.robot.quizgenerator.createquiz.main.presentation.QuestionUiState
import apps.robot.quizgenerator.domain.ImageUploadDelegate
import apps.robot.quizgenerator.domain.QuestionWithOptions
import apps.robot.quizgenerator.domain.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CreateQuestionWithOptionsViewModel(
    private val repository: QuizRepository,
    private val imageUploadDelegate: ImageUploadDelegate
) : ViewModel() {

    private val defaultOptions = listOf(
        "",
        "",
        "",
        ""
    )
    val state: MutableStateFlow<QuestionUiState> = MutableStateFlow(
        QuestionUiState.Loading
    )

    private var quizId: String? = null
    private var questionModel: QuestionWithOptions? = null

    fun onReceiveArgs(quizId: String, questionId: String?) {
        this.quizId = quizId

        viewModelScope.launch(Dispatchers.IO) {
            val quizModel = repository.getQuizModel(quizId)
            val question = quizModel.list.find { it?.id == questionId } as? QuestionWithOptions
            this@CreateQuestionWithOptionsViewModel.questionModel = question

            state.value = QuestionUiState.QuestionWithOptionsUiState(
                questionText = question?.text.orEmpty(),
                answers = question?.options ?: defaultOptions,
                isUpdatingQuestion = question != null,
                rightAnswerIndex = question?.rightAnswerIndex ?: -1,
                duration = (question?.duration ?: 30).toString(),
                points = (question?.points ?: 1).toString(),
                answerImage = repository.getDownloadUrl(question?.answerImage),
                questionImage = repository.getDownloadUrl(question?.image),
            )
        }
    }

    fun onQuestionTextChange(text: String) {
        val currentState = state.value
        if (currentState is QuestionUiState.QuestionWithOptionsUiState) {
            state.value = currentState.copy(questionText = text)
        }
    }

    fun onQuestionPointsChange(text: String) {
        val points = runCatching {
            text.trim().toInt()
        }.getOrDefault(0)

        val currentState = state.value
        if (currentState is QuestionUiState.QuestionWithOptionsUiState) {
            state.value = currentState.copy(points = points.toString())
        }
    }
    fun onQuestionDurationChange(text: String) {
        val duration = runCatching {
            text.trim().toInt()
        }.getOrDefault(0)

        val currentState = state.value
        if (currentState is QuestionUiState.QuestionWithOptionsUiState) {
            state.value = currentState.copy(duration = duration.toString())
        }
    }

    fun onCreateQuestionClick(onDone: () -> Unit) {
        viewModelScope.launch {
            val currentState = state.value
            state.value = QuestionUiState.Loading
            if (currentState is QuestionUiState.QuestionWithOptionsUiState) {
                val questionImage = currentState.questionImage
                val answerImage = currentState.answerImage

                val imagesPaths = imageUploadDelegate.upload(
                    questionImage = questionImage,
                    answerImage = answerImage,
                    quizId = quizId!!,
                    questionText = currentState.questionText,
                    )
                val questionImagePath = imagesPaths.questionPath
                val answerImagePath = imagesPaths.answerPath

                if (currentState.isUpdatingQuestion) {
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
    }

    fun onAnswerChecked(index: Int) {
        val currentState = state.value
        if (currentState is QuestionUiState.QuestionWithOptionsUiState) {
            state.value = currentState.copy(rightAnswerIndex = index)
        }
    }

    fun onAnswerTextChange(text: String, index: Int) {
        val currentState = state.value
        if (currentState is QuestionUiState.QuestionWithOptionsUiState) {
            val oldList = currentState.answers.toMutableList()
            oldList[index] = text
            state.value = currentState.copy(answers = oldList)
        }
    }

    fun onQuestionImageSelected(uri: Uri) {
        val currentState = state.value
        if (currentState is QuestionUiState.QuestionWithOptionsUiState) {
            state.value = currentState.copy(questionImage = uri)
        }
    }

    fun onAnswerImageSelected(uri: Uri) {
        val currentState = state.value
        if (currentState is QuestionUiState.QuestionWithOptionsUiState) {
            state.value = currentState.copy(answerImage = uri)
        }
    }
}