package apps.robot.quizgenerator.domain

data class QuizModel(var id: String, val name: String, var list: List<QuestionModel?>) {
    constructor(): this("", "", emptyList())
}