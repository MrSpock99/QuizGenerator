package apps.robot.quizgenerator.domain

open class QuestionModel(
    open val id: String,
    open val text: String,
    open val image: String?,
    open val voiceover: String?,
    open val type: String
)