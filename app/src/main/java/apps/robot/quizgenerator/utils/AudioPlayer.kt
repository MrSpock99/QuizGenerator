package apps.robot.quizgenerator.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    suspend fun playAudio(uri: Uri) {
        withContext(Dispatchers.Main) {
            mediaPlayer?.release() // Release any existing player
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context, uri)
                prepare()
                start()
            }
        }
    }

    fun stopAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}