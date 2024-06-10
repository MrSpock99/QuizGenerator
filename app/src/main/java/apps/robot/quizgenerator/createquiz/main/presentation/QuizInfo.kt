package apps.robot.quizgenerator.createquiz.main.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import apps.robot.quizgenerator.createquiz.presentation.CustomTextField

@Composable
fun QuizInfo(quizId: String?, viewModel: QuizInfoViewModel) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        viewModel.onReceiveArgs(quizId)
    }
    Scaffold {
        Surface(
            modifier = Modifier.padding(it)
        ) {
            Column {
                CustomTextField(modifier = Modifier.fillMaxWidth(), label = "Quiz name", text = state.name, onChange = {
                    viewModel.onNameChanged(it)
                })
                Button(onClick = { viewModel.onSaveBtnClick {
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                } }) {
                    Text(text = "Save")
                }
                Button(onClick = {
                    viewModel.onExportBtnClick {
                        Toast.makeText(context, "Exported", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(text = "Export")
                }
            }
        }
    }
}