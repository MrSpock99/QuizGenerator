package apps.robot.quizgenerator.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ImageCompressor(private val context: Context) {

    suspend fun compress(imageUri: Uri): Uri = withContext(Dispatchers.Default) {
        return@withContext Compressor.compress(context, File(imageUri.path)) {
            // Customize compression settings if needed (quality, size, etc.)
            quality(80)
            // ...
        }.toUri()
    }
}