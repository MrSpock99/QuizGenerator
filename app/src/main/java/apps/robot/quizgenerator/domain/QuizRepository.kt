package apps.robot.quizgenerator.domain

interface QuizRepository {
    fun addQuestion(questionModel: QuestionModel)
    fun exportQuiz(): String
    fun getQuizList(): List<QuizModel>
}