package apps.robot.quizgenerator.data

import apps.robot.quizgenerator.domain.OpenQuestion
import apps.robot.quizgenerator.domain.QuestionModel
import apps.robot.quizgenerator.domain.QuizModel
import apps.robot.quizgenerator.domain.QuizRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class QuizRepositoryImpl(
    private val fireStore: FirebaseFirestore
) : QuizRepository {

    override suspend fun addQuestion(quizId: String, questionModel: QuestionModel): Boolean {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { emitter ->
                fireStore.collection(QUIZ_DB)
                    .document(quizId)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val quizMap = it.result.data!!
                            val list = getQuestions(quizMap)

                            val quizModel = QuizModel(
                                id = quizMap.get("id").toString(),
                                name = quizMap.get("name").toString(),
                                list = list
                            )

                            val oldList = quizModel?.list?.toMutableList()
                            oldList?.add(questionModel)
                            quizModel?.list = oldList?.toList()!!

                            fireStore.collection(QUIZ_DB)
                                .document(quizId)
                                .set(quizModel)
                                .addOnCompleteListener {
                                    emitter.resume(it.isSuccessful)
                                }
                        }
                    }
            }
        }
    }

    override suspend fun updateQuestion(quizId: String, questionModel: QuestionModel) {
        withContext(Dispatchers.IO) {
            suspendCoroutine { emitter ->
                fireStore.collection(QUIZ_DB)
                    .document(quizId)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val quizMap = it.result.data!!
                            val list = getQuestions(quizMap)

                            val quizModel = QuizModel(
                                id = quizMap.get("id").toString(),
                                name = quizMap.get("name").toString(),
                                list = list
                            )

                            val oldList = quizModel.list.toMutableList()
                            val oldQuestionIndex = oldList.indexOf(oldList.find { it?.id == questionModel.id })
                            oldList[oldQuestionIndex] = questionModel
                            quizModel.list = oldList.toList()

                            fireStore.collection(QUIZ_DB)
                                .document(quizId)
                                .set(quizModel)
                                .addOnCompleteListener {
                                    emitter.resume(it.isSuccessful)
                                }
                        }
                    }
            }
        }
    }

    override suspend fun exportQuiz(): String {
        return ""
    }

    override suspend fun getQuizList(): List<QuizModel?> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { emitter ->
                fireStore.collection(QUIZ_DB)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            emitter.resume(it.result?.documents?.map {
                                val quizMap = it.data!!
                                val list = getQuestions(quizMap)

                                val quizModel = QuizModel(
                                    id = quizMap.get("id").toString(),
                                    name = quizMap.get("name").toString(),
                                    list = list
                                )
                                quizModel
                            } ?: listOf())
                        }
                    }
            }
        }
    }

    override suspend fun getQuizModel(id: String): QuizModel {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { emitter ->
                fireStore.collection(QUIZ_DB)
                    .document(id)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val quizMap = it.result.data!!
                            val list = getQuestions(quizMap)

                            val quizModel = QuizModel(
                                id = quizMap.get("id").toString(),
                                name = quizMap.get("name").toString(),
                                list = list
                            )
                            emitter.resume(quizModel)
                        }
                    }
            }
        }
    }

    override suspend fun createQuizModel(): QuizModel {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { emitter ->
                val id = UUID.randomUUID().toString()
                val quizModel = QuizModel(id, "test", emptyList())
                fireStore.collection(QUIZ_DB)
                    .document(id)
                    .set(quizModel)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            emitter.resume(quizModel)
                        }
                    }
            }
        }
    }

    override suspend fun saveQuiz(quizModel: QuizModel) {
        withContext(Dispatchers.IO) {
            suspendCoroutine { emitter ->
                fireStore.collection(QUIZ_DB)
                    .document(quizModel.id)
                    .set(quizModel)
                    .addOnCompleteListener {
                        emitter.resume(it.isSuccessful)
                    }
            }
        }
    }

    private fun getQuestions(quizMap: Map<String, Any>): List<QuestionModel?> {
        val list = (quizMap.get("list") as ArrayList<HashMap<String, Any>>).map {
            val question = if (it.get("answer") != null) {
                OpenQuestion(
                    id = it["id"].toString(),
                    title = it["title"].toString(),
                    text = it["text"].toString(),
                    answer = it["answer"] as ArrayList<String>,
                    voiceover = it["voiceover"].toString(),
                    image = it["image"].toString()
                )
            } else {
                null
            }
            question
        }
        return list
    }

    companion object {
        private const val QUIZ_DB = "quiz_db"
    }
}