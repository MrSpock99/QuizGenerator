package apps.robot.quizgenerator.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.modules.SerializersModule

@Serializable(with = QuestionModel.Serializer::class)
sealed class QuestionModel() {
    abstract val id: String
    abstract val text: String
    abstract val image: String?
    abstract val voiceover: String?
    abstract val type: String
    abstract val points: Int
    abstract val duration: Int
    abstract val answerImage: String?


    object Serializer : KSerializer<QuestionModel> {

        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            prettyPrint = true
            coerceInputValues = true
            classDiscriminator = "type"
            serializersModule = SerializersModule {
                polymorphic(QuestionModel::class, OpenQuestion::class, OpenQuestion.serializer())
                polymorphic(QuestionModel::class, QuestionWithOptions::class, QuestionWithOptions.serializer())
            }
        }
        override val descriptor: SerialDescriptor
            get() = PolymorphicSerializer(QuestionModel::class).descriptor
        override fun serialize(encoder: Encoder, value: QuestionModel) {
            when (value) {
                is OpenQuestion -> encoder.encodeSerializableValue(OpenQuestion.serializer(), value)
                is QuestionWithOptions -> encoder.encodeSerializableValue(QuestionWithOptions.serializer(), value)
            }
        }

        override fun deserialize(decoder: Decoder): QuestionModel {
            val jsonElement = (decoder as JsonDecoder).decodeJsonElement()
            return when (val itemType = jsonElement.jsonObject["type"]?.jsonPrimitive?.content) {
                "OpenQuestion" -> json.decodeFromJsonElement(OpenQuestion.serializer(), jsonElement)
                "QuestionWithOptions" -> json.decodeFromJsonElement(QuestionWithOptions.serializer(), jsonElement)
                else -> throw SerializationException("Unknown itemType: $itemType")
            }
        }
    }
}