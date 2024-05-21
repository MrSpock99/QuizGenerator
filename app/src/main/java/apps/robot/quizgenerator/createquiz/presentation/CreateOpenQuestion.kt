package apps.robot.quizgenerator.createquiz.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.getViewModel

@Composable
fun CreateOpenQuestion(viewModel: CreateOpenQuestionViewModel = getViewModel()) {
    Column {
        CustomTextField(label = "Question name", onChange = { viewModel.onQuestionNameChange(it) })
        CustomTextField(label = "Question text", onChange = { viewModel.onQuestionTextChange(it) })
        Button(onClick = { viewModel.onCreateQuestionClick() }) {
            Text(text = "")
        }
    }
}
