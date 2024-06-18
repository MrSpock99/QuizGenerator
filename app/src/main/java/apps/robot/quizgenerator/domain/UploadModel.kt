package apps.robot.quizgenerator.domain

import android.net.Uri

data class UploadModel(val uri: Uri, val quizId: String, val fileType: String, val questionId: String, val type: String)