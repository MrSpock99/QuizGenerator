package apps.robot.quizgenerator.domain

sealed class QuestionModel() {
    abstract val id: String
    abstract val text: String
    abstract val image: String?
    abstract val voiceover: String?
    abstract val type: String
    abstract val points: Int
    abstract val duration: Int
    abstract val answerImage: String?
}