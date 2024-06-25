package apps.robot.quizgenerator.data

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import apps.robot.quizgenerator.domain.UploadModel
import apps.robot.quizgenerator.utils.ContentTypeAdapter
import apps.robot.quizgenerator.utils.UriDeserializer
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.GsonBuilder
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UploadWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val imageCompressor = ImageCompressor(applicationContext)

        val gson = GsonBuilder()
            .registerTypeAdapter(Uri::class.java, UriDeserializer())
            .registerTypeAdapter(UploadManager.ContentType::class.java, ContentTypeAdapter())
            .create()
        val data = gson.fromJson(
            inputData.getString("upload_model"),
            UploadModel::class.java
        )
        val storage = Firebase.storage
        var storageRef = storage.reference
        val contentType = when (data.contentType) {
            UploadManager.ContentType.Image -> "image"
            UploadManager.ContentType.Audio -> "audio"
        }
        val path = "${data.quizId}/${data.questionId}/${contentType}/${data.type}"
        var imagesRef: StorageReference? =
            storageRef.child(
                path
            )

        val uploadTask = imagesRef
            ?.putFile(data.uri)
        uploadTask?.await()

        return Result.success(Data.Builder().putString("path", getDownloadUrl(imagesRef).toString()).build())
    }

    private suspend fun getDownloadUrl(path: StorageReference?): Uri? = suspendCancellableCoroutine { continuation ->

       (path)?.downloadUrl?.addOnCompleteListener {
            if (it.isSuccessful) {
                continuation.resume(it.result)
            } else {
                continuation.resumeWithException(it.exception!!)
            }
        }
    }
}