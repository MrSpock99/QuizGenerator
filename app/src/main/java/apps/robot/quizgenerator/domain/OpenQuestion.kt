package apps.robot.quizgenerator.domain

import kotlinx.serialization.Serializable

@Serializable
data class OpenQuestion(
    override var id: String = "",
    override val text: String = "",
    val answer: List<String> = listOf(),
    override val image: String? = "",
    override val voiceover: String? = "",
    override val points: Int,
    override val duration: Int,
    override val answerImage: String?,
    override val questionAudio: String?,
    override val questionVideo: String?,
    override val answerAudio: String?,
    override val answerVideo: String?,
) :  QuestionModel() {

    override val type: String
        get() = QuestionType.OpenQuestion.name
}