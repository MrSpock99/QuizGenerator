package apps.robot.quizgenerator.createquiz.questionwithoptions

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import apps.robot.quizgenerator.R
import apps.robot.quizgenerator.createquiz.base.CreateQuestionViewModel
import apps.robot.quizgenerator.createquiz.main.presentation.CustomTextField
import coil.compose.rememberAsyncImagePainter
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuestionWithOptions(
    quizId: String,
    questionId: String?,
    navController: NavController,
    viewModel: CreateQuestionWithOptionsViewModel = getViewModel()
) {

    val state by viewModel.state.collectAsState()
    val questionImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        Log.d("TAG", "URI: $uri")
        viewModel.onQuestionImageSelected(uri!!)
    }
    val answerImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        Log.d("TAG", "URI: $uri")
        viewModel.onAnswerImageSelected(uri!!)
    }
    val questionAudioLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        Log.d("TAG", "URI: $uri")
        viewModel.onQuestionAudioSelected(uri!!)
    }
    val answerAudioLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        Log.d("TAG", "URI: $uri")
        viewModel.onAnswerAudioSelected(uri!!)
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.onReceiveArgs(quizId, questionId)
    }

    when (state) {
        CreateQuestionViewModel.QuestionUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(100.dp))
            }
        }

        is CreateQuestionViewModel.QuestionUiState.QuestionWithOptionsUiState -> {
            val currentState = state as CreateQuestionViewModel.QuestionUiState.QuestionWithOptionsUiState
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "Create open question")
                        }, navigationIcon = {
                            IconButton(
                                modifier = Modifier
                                    .alpha(ContentAlpha.medium),
                                onClick = {
                                    navController.popBackStack()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = "Back button"
                                )
                            }
                        })
                }
            ) {
                Surface(
                    modifier = Modifier.padding(it)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),

                        ) {
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Question text",
                            onChange = { viewModel.onQuestionTextChange(it) },
                            text = currentState.questionText
                        )

                        currentState.answers.forEachIndexed { index, option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onAnswerChecked(index)
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = index == currentState.rightAnswerIndex,
                                    onCheckedChange = { viewModel.onAnswerChecked(index) }
                                )
                                CustomTextField(
                                    text = option,
                                    modifier = Modifier.padding(start = 8.dp),
                                    label = "",
                                    onChange = {
                                        viewModel.onAnswerTextChange(it, index)
                                    })
                            }
                        }
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Question points",
                            onChange = { viewModel.onQuestionPointsChange(it) },
                            text = currentState.points.toString(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Number
                            )
                        )
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Question duration",
                            onChange = { viewModel.onQuestionDurationChange(it) },
                            text = currentState.duration.toString(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Number
                            )
                        )
                        Image(
                            modifier = Modifier.size(100.dp),
                            painter = rememberAsyncImagePainter(currentState.questionImage),
                            contentDescription = "My Image"
                        )
                        Button(onClick = {
                            questionImageLauncher.launch("image/*")
                        }) {
                            val text = "Question image"
                            Text(text = text)
                        }
                        Image(
                            modifier = Modifier.size(100.dp),
                            painter = rememberAsyncImagePainter(currentState.answerImage, onState = {
                                Log.d("MYTAG", "Image state: $it")
                            }),
                            contentDescription = "My Image"
                        )
                        Button(onClick = {
                            answerImageLauncher.launch("image/*")
                        }) {
                            val text = "Answer image"
                            Text(text = text)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = currentState.questionAudio.toString().take(10)
                            )
                            if (currentState.questionAudio != null) {
                                val btnState = currentState.playAudioState
                                Button(
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        if (btnState == CreateQuestionViewModel.QuestionUiState.AudioState.Paused) {
                                            viewModel.onPlayAudioClicked(currentState.questionAudio)
                                        } else {
                                            viewModel.onStopAudioClicked()
                                        }
                                    }) {
                                    Text("Play/Stop audio")
                                }
                            }
                            Button(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    questionAudioLauncher.launch("audio/*")
                                }) {
                                val text = "Question audio"
                                Text(text = text)
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = currentState.answerAudio.toString().take(10)
                            )
                            if (currentState.answerAudio != null) {
                                val btnState = currentState.playAudioState
                                Button(
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        if (btnState == CreateQuestionViewModel.QuestionUiState.AudioState.Paused) {
                                            viewModel.onPlayAudioClicked(currentState.answerAudio)
                                        } else {
                                            viewModel.onStopAudioClicked()
                                        }
                                    }) {
                                    Text("Play/Stop audio")
                                }
                            }
                            Button(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    answerAudioLauncher.launch("audio/*")
                                }) {
                                val text = "Answer audio"
                                Text(text = text)
                            }
                        }

                        Button(onClick = {
                            viewModel.onCreateQuestionClick {
                                navController.popBackStack()
                            }
                        }) {
                            val text = if (currentState.isUpdatingQuestion) {
                                "Update"
                            } else {
                                "Create"
                            }
                            Text(text = text)
                        }
                    }
                }
            }
        }

        else -> {
            // nothing
        }
    }

}