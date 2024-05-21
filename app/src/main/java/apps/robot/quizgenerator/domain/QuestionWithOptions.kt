package apps.robot.quizgenerator.domain

class QuestionWithOptions(title: String, text: String, options: List<Option>, image: String?, voiceover: String?) :
    QuestionModel(title, text, image, voiceover) {

    enum class Answer {
        TRUE,
        FALSE
    }

    data class Option(
        val text: String,
        val rightAnswer: Answer = Answer.FALSE
    )}
