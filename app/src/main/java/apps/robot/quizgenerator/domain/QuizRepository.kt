package apps.robot.quizgenerator.domain

import android.net.Uri

interface QuizRepository {
    suspend fun addQuestion(quizId: String, questionModel: QuestionModel): Boolean
    suspend fun updateQuestion(quizId: String, questionModel: QuestionModel)
    suspend fun exportQuiz(quizId: String): String
    suspend fun getQuizList(): List<QuizModel?>
    suspend fun getQuizModel(id: String): QuizModel
    suspend fun createQuizModel(): QuizModel
    suspend fun saveQuiz(quizModel: QuizModel)
    suspend fun getDownloadUrl(path: String?): Uri?
}