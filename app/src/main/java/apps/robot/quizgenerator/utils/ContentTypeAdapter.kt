package apps.robot.quizgenerator.utils

import apps.robot.quizgenerator.data.UploadManager
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

// Define a TypeAdapter for ContentType
class ContentTypeAdapter : TypeAdapter<UploadManager.ContentType>() {

    override fun write(out: JsonWriter, value: UploadManager.ContentType?) {
        if (value == null) {
            out.nullValue()
            return
        }
        // Serialize based on the specific ContentType object
        when (value) {
            is UploadManager.ContentType.Image -> out.value("image")
            is UploadManager.ContentType.Audio -> out.value("audio")
        }
    }

    override fun read(`in`: JsonReader): UploadManager.ContentType? {
        // Deserialize based on the value read from JSON
        return when (`in`.nextString()) {
            "image" -> UploadManager.ContentType.Image
            "audio" -> UploadManager.ContentType.Audio
            else -> null // Handle unknown types as needed
        }
    }
}