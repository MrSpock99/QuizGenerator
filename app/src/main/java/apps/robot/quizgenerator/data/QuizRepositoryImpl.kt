package apps.robot.quizgenerator.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat
import apps.robot.quizgenerator.domain.OpenQuestion
import apps.robot.quizgenerator.domain.QuestionModel
import apps.robot.quizgenerator.domain.QuestionWithOptions
import apps.robot.quizgenerator.domain.QuizModel
import apps.robot.quizgenerator.domain.QuizRepository
import apps.robot.quizgenerator.domain.QuizRound
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import qrcode.QRCode
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class QuizRepositoryImpl(
    private val fireStore: FirebaseFirestore,
    private val context: Context,
    private val storage: FirebaseStorage
) : QuizRepository {

    override suspend fun addQuestion(quizId: String, questionModel: QuestionModel): Boolean {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { emitter ->
                fireStore.collection(QUIZ_DB)
                    .document(quizId)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            launch {
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
                                val list = getQuestions(quizMap, true)

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

    override suspend fun exportQuiz(quizId: String): String = withContext(Dispatchers.IO) {
        val quizModel = getQuizMap(quizId)

        val json = Gson().toJson(quizModel)
        // By default, QRCodes are rendered as PNGs.
        val pngBytes = QRCode.ofCircles()
            .build(quizModel["id"].toString())
            .render()
        writeBytesToSharedStorage(context, "${quizModel["name"]}.png", pngBytes.getBytes())
        return@withContext json
    }

    override suspend fun getQuizList(): List<QuizModel?> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { emitter ->
                fireStore.collection(QUIZ_DB)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            launch {
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
    }

    override suspend fun getQuizModel(id: String): QuizModel {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { emitter ->
                fireStore.collection(QUIZ_DB)
                    .document(id)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            launch {
                                val quizMap = it.result.data!!
                                val list = getQuestions(quizMap, true)

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
    }

    override suspend fun createQuizModel(): QuizModel {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { emitter ->
                val id = UUID.randomUUID().toString()
                val quizModel = QuizModel(id, "", emptyList())
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

    private suspend fun getQuizMap(id: String): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { emitter ->
                fireStore.collection(QUIZ_DB)
                    .document(id)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val quizMap = it.result.data!!

                            emitter.resume(quizMap)
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

    private fun getQuestions(quizMap: Map<String, Any>, getDownloadUrls: Boolean = true): List<QuestionModel?> {
        val list = (quizMap.get("list") as ArrayList<HashMap<String, Any>>).map {
            val questionImagePath = it["image"]?.toString()
            val answerImagePath = it["answerImage"]?.toString()
            /*   val questionImageRefJob = if (questionImagePath != "null" && questionImagePath.isNotBlank() && getDownloadUrls) {
                       async {
                           getDownloadUrl(storage.reference.child(questionImagePath).downloadUrl)
                       }
                   } else {
                       null
                   }
                   val answerImageRefJob = if (answerImagePath != "null" && answerImagePath.isNotBlank() && getDownloadUrls) {
                       async {
                           getDownloadUrl(storage.reference.child(answerImagePath).downloadUrl)
                       }
                   } else {
                       null
                   }
                   val questionImageRef = questionImageRefJob?.await()
                   val answerImageRef = answerImageRefJob?.await()*/

            val question = if (it.get("answer") != null) {
                OpenQuestion(
                    id = it["id"].toString(),
                    text = it["text"].toString(),
                    answer = it["answer"] as ArrayList<String>,
                    voiceover = it["voiceover"].toString(),
                    image = questionImagePath,
                    points = it["points"]?.toString()?.toInt() ?: 1,
                    duration = it["duration"]?.toString()?.toInt() ?: 30,
                    answerImage = answerImagePath,
                    questionAudio = it["questionAudio"]?.toString(),
                    answerAudio = it["answerAudio"]?.toString(),
                    questionVideo = it["questionVideo"]?.toString(),
                    answerVideo = it["answerVideo"]?.toString()
                )
            } else if (it.get("options") != null) {
                QuestionWithOptions(
                    id = it["id"].toString(),
                    text = it["text"].toString(),
                    options = it["options"] as ArrayList<String>,
                    rightAnswerIndex = (it["rightAnswerIndex"] as Long).toInt(),
                    voiceover = it["voiceover"].toString(),
                    image = questionImagePath,
                    points = it["points"]?.toString()?.toInt() ?: 1,
                    duration = it["duration"]?.toString()?.toInt() ?: 30,
                    answerImage = answerImagePath,
                    questionAudio = it["questionAudio"]?.toString(),
                    answerAudio = it["answerAudio"]?.toString(),
                    questionVideo = it["questionVideo"]?.toString(),
                    answerVideo = it["answerVideo"]?.toString()
                )
            } else {
                QuizRound(
                    id = it["id"].toString(),
                    text = it["text"].toString(),
                    title = it["title"].toString(),
                    image = questionImagePath
                )
            }
            question
        }
        return list
    }

    override suspend fun getDownloadUrl(path: String?): Uri? = suspendCancellableCoroutine { continuation ->
        if (path.isNullOrBlank()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }
        storage.reference.child(path).downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) {
                continuation.resume(it.result)
            } else {
                continuation.resumeWithException(it.exception!!)
            }
        }
    }

    fun createAppDirectoryInDownloads(context: Context): File? {
        val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val appDirectory = File(downloadsDirectory, "QuizGenerator")

        if (!appDirectory.exists()) {
            val directoryCreated = appDirectory.mkdir()
            if (!directoryCreated) {
                // Failed to create the directory
                return null
            }
        }

        return appDirectory
    }

    fun createFileInAppDirectory(context: Context, fileName: String): File? {
        val appDirectory = createAppDirectoryInDownloads(context)
        if (appDirectory != null) {
            val file = File(appDirectory, fileName)
            try {
                if (!file.exists()) {
                    val fileCreated = file.createNewFile()
                    if (!fileCreated) {
                        // Failed to create the file
                        return null
                    }
                }
                return file
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    suspend fun writeBytesToSharedStorage(context: Context, fileName: String, bytes: ByteArray) {
        withContext(Dispatchers.IO) {
            val sharedStorageDir = ContextCompat.getExternalFilesDirs(context, null)[0]
            val file = File(sharedStorageDir, fileName)

            try {
                FileOutputStream(file).use { outputStream ->
                    outputStream.write(bytes)
                }
                println("Bytes written to file: ${file.absolutePath}")
            } catch (e: Exception) {
                println("Error writing bytes to file: ${e.message}")
                // Handle the exception appropriately (e.g., log the error, show a message to the user)
            }
        }
    }

    companion object {
        private const val QUIZ_DB = "quiz_db"
    }
}