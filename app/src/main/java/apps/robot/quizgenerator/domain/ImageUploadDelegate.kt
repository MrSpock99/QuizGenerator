package apps.robot.quizgenerator.domain

import android.net.Uri
import apps.robot.quizgenerator.data.UploadManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class ImageUploadDelegate(
    private val uploadManager: UploadManager
) {

    suspend fun upload(
        questionImage: Uri?,
        answerImage: Uri?,
        quizId: String,
        questionText: String,
    ): ImagePaths = withContext(Dispatchers.IO) {
        val questionImagePathJob = if (questionImage != null && questionImage.scheme != "https") {
            async {
                uploadManager.uploadFileFromUri(
                    quizId = quizId,
                    fileType = "image",
                    questionId = "question_$questionText",
                    uri = questionImage,
                    type = "question_image",
                )
            }
        } else {
            null
        }

        val answerImagePathJob = if (answerImage != null && answerImage.scheme != "https") {
            async {
                uploadManager.uploadFileFromUri(
                    quizId = quizId,
                    fileType = "image",
                    questionId = "question_$questionText",
                    uri = answerImage,
                    type = "answer_image",
                )
            }
        } else {
            null
        }

        return@withContext ImagePaths(
            questionImagePathJob?.await(),
            answerImagePathJob?.await()
        )
    }

    data class ImagePaths(val questionPath: String?, val answerPath: String?)
}