package apps.robot.quizgenerator.createquiz.presentation

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable

@Composable
fun CustomTextField(label: String, onChange: (String) -> Unit, text: String) {
    TextField(
        value = text,
        onValueChange = {
            onChange(it)
        },
        label = { Text(label) }
    )
}