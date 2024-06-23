package apps.robot.quizgenerator.domain

import kotlinx.serialization.Serializable

@Serializable
data class QuizRound(
    override val id: String,
    override val text: String,
    override val image: String?,
    val title: String
) : QuestionModel() {

    override val type: String
        get() = QuestionType.Round.name
}