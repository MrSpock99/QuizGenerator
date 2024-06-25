package apps.robot.quizgenerator.createquiz.round

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import apps.robot.quizgenerator.createquiz.main.presentation.CustomTextField
import coil.compose.rememberAsyncImagePainter
import org.koin.androidx.compose.get

@Composable
fun CreateQuizRoundComposable(
    quizId: String,
    roundId: String?,
    viewModel: CreateQuizRoundViewModel = get(),
    navController: NavHostController
) {

    val uiState by viewModel.uiState.collectAsState()

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        Log.d("TAG", "URI: $uri")
        viewModel.onImageSelected(uri!!)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.onReceiveArgs(quizId, roundId)

    }
    Column {
        Image(
            modifier = Modifier.size(100.dp),
            painter = rememberAsyncImagePainter(uiState.image),
            contentDescription = "My Image"
        )
        Button(onClick = {
            imageLauncher.launch("image/*")
        }) {
            val text = "Image"
            Text(text = text)
        }
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            label = "Title",
            onChange = { viewModel.onTitleChange(it) },
            text = uiState.title
        )
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            label = "Text",
            onChange = { viewModel.onTextChange(it) },
            text = uiState.text
        )
        Button(onClick = {
            viewModel.onCreateClick {
                navController.popBackStack()
            }
        }) {
            val text = "Create"
            Text(text = text)
        }
    }
}