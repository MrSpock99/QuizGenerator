package apps.robot.quizgenerator.data

import android.content.SharedPreferences
import apps.robot.quizgenerator.domain.QuizRepository
import apps.robot.quizgenerator.domain.QuestionModel
import apps.robot.quizgenerator.domain.QuizModel

class QuizRepositoryImpl(
    private val preferences: SharedPreferences
) : QuizRepository {

    override fun addQuestion(questionModel: QuestionModel) {
        //preferences.edit().putString()
    }

    override fun exportQuiz(): String {
        return ""
    }

    override fun getQuizList(): List<QuizModel> {
        return emptyList()
    }
}