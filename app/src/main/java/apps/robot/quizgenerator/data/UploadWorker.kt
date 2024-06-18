package apps.robot.quizgenerator.data

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import apps.robot.quizgenerator.domain.UploadModel
import apps.robot.quizgenerator.utils.UriDeserializer
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.GsonBuilder
import kotlinx.coroutines.tasks.await

class UploadWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val imageCompressor = ImageCompressor(applicationContext)

        val gson = GsonBuilder()
            .registerTypeAdapter(Uri::class.java, UriDeserializer())
            .create()
        val data = gson.fromJson(
            inputData.getString("upload_model"),
            UploadModel::class.java
        )
        val storage = Firebase.storage
        var storageRef = storage.reference
        val path = "${data.quizId}/${data.questionId}/${data.fileType}/${data.type}"
        var imagesRef: StorageReference? =
            storageRef.child(
                path
            )

        val uploadTask = imagesRef?.putFile(data.uri)
        uploadTask?.await()

      /*  uploadTask?.addOnSuccessListener {
            return@addOnSuccessListener Result.success(Data.Builder().putString("path", path).build())

            //continuation.resume(Result.success(Data.Builder().putString("path", path).build()))
        }?.addOnFailureListener {
            continuation.resumeWithException(it)
        }*/
        return Result.success(Data.Builder().putString("path", path).build())
    }

}