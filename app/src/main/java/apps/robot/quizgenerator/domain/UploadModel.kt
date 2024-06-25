package apps.robot.quizgenerator.domain

import android.net.Uri
import apps.robot.quizgenerator.data.UploadManager

data class UploadModel(
    val uri: Uri,
    val quizId: String,
    val contentType: UploadManager.ContentType,
    val questionId: String,
    val type: String
)