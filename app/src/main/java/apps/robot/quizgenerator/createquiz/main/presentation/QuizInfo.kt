package apps.robot.quizgenerator.createquiz.main.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import apps.robot.quizgenerator.createquiz.presentation.CustomTextField
import org.koin.androidx.compose.getViewModel

@Composable
fun QuizInfo(quizId: String?, viewModel: QuizInfoViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.onReceiveArgs(quizId)
    }
    Scaffold {
        Surface(
            modifier = Modifier.padding(it)
        ) {
            Column {
                CustomTextField(label = "Quiz name", text = state.name, onChange = {
                    viewModel.onNameChanged(it)
                })
                Button(onClick = { viewModel.onSaveBtnClick() }) {
                    Text(text = "Save")
                }
            }
        }
    }
}