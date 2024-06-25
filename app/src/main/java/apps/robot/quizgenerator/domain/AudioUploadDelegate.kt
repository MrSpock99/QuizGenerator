package apps.robot.quizgenerator.domain

import android.net.Uri
import apps.robot.quizgenerator.data.UploadManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class AudioUploadDelegate(
    private val uploadManager: UploadManager
) {
    suspend fun upload(
        questionAudio: Uri?,
        answerAudio: Uri?,
        quizId: String,
        questionText: String,
    ): AudioPaths = withContext(Dispatchers.IO) {
        val questionPath = if (questionAudio != null && questionAudio.scheme != "https") {
            async {
                uploadManager.uploadFileFromUri(
                    quizId = quizId,
                    contentType = UploadManager.ContentType.Audio,
                    questionId = "question_$questionText",
                    uri = questionAudio,
                    type = "question_audio",
                )
            }
        } else if (questionAudio?.scheme == "https") {
            async {
                questionAudio.toString()
            }
        } else {
            null
        }

        val answerPath = if (answerAudio != null && answerAudio.scheme != "https") {
            async {
                uploadManager.uploadFileFromUri(
                    quizId = quizId,
                    contentType = UploadManager.ContentType.Audio,
                    questionId = "question_$questionText",
                    uri = answerAudio,
                    type = "answer_audio",
                )
            }
        }  else if (answerAudio?.scheme == "https") {
            async {
                answerAudio.toString()
            }
        } else {
            null
        }

        return@withContext AudioPaths(
            questionPath?.await(),
            answerPath?.await()
        )
    }

    data class AudioPaths(val questionPath: String?, val answerPath: String?)
}