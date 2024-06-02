package apps.robot.quizgenerator.domain

interface QuizRepository {
    suspend fun addQuestion(quizId: String, questionModel: QuestionModel): Boolean
    suspend fun updateQuestion(quizId: String, questionModel: QuestionModel)
    suspend fun exportQuiz(): String
    suspend fun getQuizList(): List<QuizModel?>
    suspend fun getQuizModel(id: String): QuizModel
    suspend fun createQuizModel(): QuizModel
    suspend fun saveQuiz(quizModel: QuizModel)
}