package apps.robot.quizgenerator.createquiz.openquestion

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apps.robot.quizgenerator.createquiz.main.presentation.QuestionUiState
import apps.robot.quizgenerator.domain.ImageUploadDelegate
import apps.robot.quizgenerator.domain.OpenQuestion
import apps.robot.quizgenerator.domain.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CreateOpenQuestionViewModel(
    private val repository: QuizRepository,
    private val imageUploadDelegate: ImageUploadDelegate
) : ViewModel() {

    var state: MutableStateFlow<QuestionUiState> =
        MutableStateFlow(
            QuestionUiState.Loading
        )
        private set

    private var questionId: String? = null
    private var questionModel: OpenQuestion? = null
    private var quizId: String? = null

    fun onReceiveArgs(id: String, questionId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val quiz = repository.getQuizModel(id)
            this@CreateOpenQuestionViewModel.questionId = questionId
            this@CreateOpenQuestionViewModel.quizId = quiz.id

            val question = quiz.list.find { it?.id == questionId } as? OpenQuestion
            questionModel = question

            state.value = QuestionUiState.OpenQuestionUiState(
                questionText = question?.text.orEmpty(),
                answers = question?.answer.orEmpty(),
                isUpdatingQuestion = question != null,
                duration = (question?.duration ?: 30).toString(),
                points = (question?.points ?: 1).toString(),
                answerImage = repository.getDownloadUrl(question?.answerImage),
                questionImage = repository.getDownloadUrl(question?.image),
                currentAnswer = ""
            )
        }

    }

    fun onQuestionTextChange(text: String) {
        val currentState = state.value
        if (currentState is QuestionUiState.OpenQuestionUiState) {
            state.value = currentState.copy(questionText = text)
        }
    }

    fun onQuestionAnswerAdd(text: String) {
        val currentState = state.value
        if (currentState is QuestionUiState.OpenQuestionUiState) {
            val old = currentState.answers.toMutableList()
            old.add(text.trim().lowercase())
            state.value = currentState.copy(answers = old)
        }

    }

    fun onQuestionAnswerChange(text: String) {
        val currentState = state.value
        if (currentState is QuestionUiState.OpenQuestionUiState) {
            state.value = currentState.copy(currentAnswer = text)
        }
    }

    fun onQuestionPointsChange(text: String) {
        val points = runCatching {
            text.trim().toInt()
        }.getOrDefault(0).toString()

        val currentState = state.value
        if (currentState is QuestionUiState.OpenQuestionUiState) {
            state.value = currentState.copy(points = points)
        }
    }

    fun onCreateQuestionClick(onDone: () -> Unit) {
        viewModelScope.launch {
            val questionImage = state.value.questionImage
            val answerImage = state.value.answerImage
            val currentState = state.value

            state.value = QuestionUiState.Loading

            val imagesPaths = imageUploadDelegate.upload(
                questionImage = questionImage,
                answerImage = answerImage,
                quizId = quizId!!,
                questionText = currentState.questionText,

            )
            val questionImagePath = imagesPaths.questionPath
            val answerImagePath = imagesPaths.answerPath

            val quizId = quizId!!
            if (currentState is QuestionUiState.OpenQuestionUiState) {
                if (currentState.isUpdatingQuestion) {
                    var model = questionModel?.copy(
                        text = currentState.questionText.trim(),
                        answer = currentState.answers,
                        duration = currentState.duration.trim().toInt(),
                        points = currentState.points.trim().toInt(),
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
                    val job = viewModelScope.launch(Dispatchers.IO) {
                        repository.updateQuestion(quizId, model)
                    }
                    job.join()
                    onDone()
                } else {
                    val model = OpenQuestion(
                        id = UUID.randomUUID().toString(),
                        text = currentState.questionText.trim(),
                        answer = currentState.answers,
                        voiceover = null,
                        points = currentState.points.trim().toInt(),
                        duration = currentState.duration.trim().toInt(),
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
    }

    fun onDeleteAnswerClick(answer: String) {
        val currentState = state.value
        if (currentState is QuestionUiState.OpenQuestionUiState) {
            val old = currentState.answers.toMutableList()
            old.remove(answer)
            state.value = currentState.copy(answers = old)
        }
    }

    fun onQuestionDurationChange(text: String) {
        val duration = runCatching {
            text.trim().toInt()
        }.getOrDefault(0).toString()
        val currentState = state.value
        if (currentState is QuestionUiState.OpenQuestionUiState) {
            state.value = currentState.copy(duration = duration)
        }
    }

    fun onQuestionImageSelected(uri: Uri) {
        val currentState = state.value
        if (currentState is QuestionUiState.OpenQuestionUiState) {
            state.value = currentState.copy(
                questionImage = uri
            )
        }

    }

    fun onAnswerImageSelected(uri: Uri) {
        val currentState = state.value
        if (currentState is QuestionUiState.OpenQuestionUiState) {
            state.value = currentState.copy(
                answerImage = uri
            )
        }
    }
}