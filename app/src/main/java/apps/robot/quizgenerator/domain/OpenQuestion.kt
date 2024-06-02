package apps.robot.quizgenerator.domain

data class OpenQuestion(
    override var id: String = "",
    override val text: String = "",
    val answer: List<String> = listOf(),
    override val image: String? = "",
    override val voiceover: String? = ""
) :
    QuestionModel(id, text, image, voiceover) {
    constructor(answer: String) : this("", "", listOf(), null, null)
}