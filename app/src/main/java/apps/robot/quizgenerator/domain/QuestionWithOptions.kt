package apps.robot.quizgenerator.domain

import kotlinx.serialization.Serializable

@Serializable
data class QuestionWithOptions(
    override val id: String,
    override val text: String,
    val options: List<String>,
    val rightAnswerIndex: Int,
    override val image: String?,
    override val voiceover: String?,
    override val points: Int,
    override val duration: Int,
    override val answerImage: String?,
) :
    QuestionModel() {

    override val type: String
        get() = QuestionType.QuestionWithOptions.name
}
