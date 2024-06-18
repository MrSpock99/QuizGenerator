package apps.robot.quizgenerator.domain

data class OpenQuestion(
    override var id: String = "",
    override val text: String = "",
    val answer: List<String> = listOf(),
    override val image: String? = "",
    override val voiceover: String? = "",
    override val points: Int,
    override val duration: Int,
    override val answerImage: String?,
) :  QuestionModel() {

    override val type: String
        get() = "OpenQuestion"
}