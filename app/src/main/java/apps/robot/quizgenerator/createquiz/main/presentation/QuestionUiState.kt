package apps.robot.quizgenerator.createquiz.main.presentation

import android.net.Uri

sealed interface QuestionUiState {
    val questionText: String
    val points: String
    val duration: String
    val questionImage: Uri?
    val answerImage: Uri?
    val isUpdatingQuestion: Boolean

    data object Loading : QuestionUiState {
        override val questionText: String
            get() = ""
        override val points: String
            get() = ""
        override val duration: String
            get() = ""
        override val questionImage: Uri?
            get() = null
        override val answerImage: Uri?
            get() = null
        override val isUpdatingQuestion: Boolean
            get() = false
    }

    data class OpenQuestionUiState(
        override val questionText: String,
        override val points: String,
        override val duration: String,
        override val questionImage: Uri?,
        override val answerImage: Uri?,
        override val isUpdatingQuestion: Boolean,
        val answers: List<String>,
        val currentAnswer: String,
    ) : QuestionUiState

    data class QuestionWithOptionsUiState(
        override val questionText: String,
        override val points: String,
        override val duration: String,
        override val questionImage: Uri?,
        override val answerImage: Uri?,
        override val isUpdatingQuestion: Boolean,
        val answers: List<String>,
        val rightAnswerIndex: Int,

    ) : QuestionUiState
}
