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
sealed class QuestionModel {
    open val id: String = ""
    open val text: String = ""
    open val image: String? = ""
    open val voiceover: String? = ""
    open val type: String = ""
    open val points: Int = 0
    open val duration: Int = 0
    open val answerImage: String? = null
    open val questionAudio: String? = null
    open val questionVideo: String? = null
    open val answerAudio: String? = null
    open val answerVideo: String? = null

    object Serializer : KSerializer<QuestionModel> {

        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            prettyPrint = true
            coerceInputValues = true
            classDiscriminator = "type"
            explicitNulls = false
            serializersModule = SerializersModule {
                polymorphic(QuestionModel::class, OpenQuestion::class, OpenQuestion.serializer())
                polymorphic(QuestionModel::class, QuestionWithOptions::class, QuestionWithOptions.serializer())
                polymorphic(QuestionModel::class, QuizRound::class, QuizRound.serializer())
            }
        }
        override val descriptor: SerialDescriptor
            get() = PolymorphicSerializer(QuestionModel::class).descriptor
        override fun serialize(encoder: Encoder, value: QuestionModel) {
            when (value) {
                is OpenQuestion -> encoder.encodeSerializableValue(OpenQuestion.serializer(), value)
                is QuestionWithOptions -> encoder.encodeSerializableValue(QuestionWithOptions.serializer(), value)
                is QuizRound -> encoder.encodeSerializableValue(QuizRound.serializer(), value)
            }
        }

        override fun deserialize(decoder: Decoder): QuestionModel {
            val jsonElement = (decoder as JsonDecoder).decodeJsonElement()
            return when (val itemType = jsonElement.jsonObject["type"]?.jsonPrimitive?.content) {
                QuestionType.OpenQuestion.name -> json.decodeFromJsonElement(OpenQuestion.serializer(), jsonElement)
                QuestionType.QuestionWithOptions.name -> json.decodeFromJsonElement(
                    QuestionWithOptions.serializer(), jsonElement)
                QuestionType.Round.name -> json.decodeFromJsonElement(
                    QuizRound.serializer(), jsonElement)
                else -> throw SerializationException("Unknown itemType: $itemType")
            }
        }
    }
    @Serializable
    enum class QuestionType {
        OpenQuestion, QuestionWithOptions, Round, None
    }
}