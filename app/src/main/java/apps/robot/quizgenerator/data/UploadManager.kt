package apps.robot.quizgenerator.data

import android.net.Uri
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import apps.robot.quizgenerator.domain.UploadModel
import apps.robot.quizgenerator.utils.UriSerializer
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UploadManager(private val workManager: WorkManager) {

    suspend fun uploadFileFromUri(
        quizId: String,
        fileType: String,
        questionId: String,
        type: String,
        uri: Uri,
    ): String = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val gson = GsonBuilder()
                .registerTypeAdapter(Uri::class.java, UriSerializer())
                .create()
            val uploadModel =
                UploadModel(uri = uri, quizId = quizId, fileType = fileType, questionId = questionId, type = type)
            val workData = Data.Builder().putString(
                "upload_model", gson.toJson(uploadModel)
            ).build()
            val uploadWorkRequest =
                OneTimeWorkRequest.Builder(UploadWorker::class.java)
                    .setInputData(workData)
                    .addTag(UploadWorker::class.toString())
                    .build()
            workManager.enqueue(uploadWorkRequest)

            workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id).observeForever { workInfo ->
                if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                    val outputData = workInfo.outputData
                    val uploadResult = outputData.getString("path")

                    continuation.resume(uploadResult!!)
                } else if (workInfo != null && workInfo.state == WorkInfo.State.FAILED) {
                    continuation.resumeWithException(Exception("error while uploading"))
                }
            }
        }
    }
}