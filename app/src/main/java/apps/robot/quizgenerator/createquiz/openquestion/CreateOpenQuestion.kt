package apps.robot.quizgenerator.createquiz.openquestion

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import apps.robot.quizgenerator.createquiz.presentation.CustomTextField
import org.koin.androidx.compose.getViewModel

@Composable
fun CreateOpenQuestion(id: String, viewModel: CreateOpenQuestionViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.onReceiveArgs(id)
    }
    Column {
        CustomTextField(label = "Question name", onChange = { viewModel.onQuestionNameChange(it) }, text = state.questionName)
        CustomTextField(label = "Question text", onChange = { viewModel.onQuestionTextChange(it) }, text = state.questionText)
        CustomTextField(label = "Question answer", onChange = { viewModel.onQuestionAnswerChange(it) }, text = "")
        Button(onClick = { viewModel.onCreateQuestionClick() }) {
            Text(text = "Create question")
        }
    }
}
