package apps.robot.quizgenerator.domain

data class QuestionWithOptions(
    override val id: String,
    override val text: String,
    val options: List<String>,
    val rightAnswerIndex: Int,
    override val image: String?,
    override val voiceover: String?,
    override val type: String,
    override val points: Int,
    override val duration: Int
) :
    QuestionModel(id, text, image, voiceover, type, points, duration) {

    constructor() : this("", "", listOf(),-1, null, null, "", 0, 0)
}
