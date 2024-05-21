package apps.robot.quizgenerator.domain

class OpenQuestion(title: String, text: String, answer: String, image: String?, voiceover: String?) :
    QuestionModel(title, text, image, voiceover) {
}