package apps.robot.quizgenerator.createquiz.openquestion

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.Button
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
import apps.robot.quizgenerator.createquiz.main.presentation.QuestionUiState
import apps.robot.quizgenerator.createquiz.presentation.CustomTextField
import apps.robot.quizgenerator.createquiz.presentation.TextFieldWithBubble
import coil.compose.rememberAsyncImagePainter
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOpenQuestion(
    quizId: String,
    questionId: String?,
    navController: NavController,
    viewModel: CreateOpenQuestionViewModel = getViewModel()
) {
    val questionImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        Log.d("TAG", "URI: $uri")
        viewModel.onQuestionImageSelected(uri!!)
    }
    val answerImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        Log.d("TAG", "URI: $uri")
        viewModel.onAnswerImageSelected(uri!!)
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.onReceiveArgs(quizId, questionId)
    }
    val state by viewModel.state.collectAsState()
    when (state) {
        QuestionUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(100.dp))
            }
        }

        is QuestionUiState.OpenQuestionUiState -> {
            val currentState = state as QuestionUiState.OpenQuestionUiState
            Scaffold(
                topBar = {
                    TopAppBar(title = {
                        Text(text = "Create open question")
                    },
                              navigationIcon = {
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
                    Column {
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Question text",
                            onChange = { viewModel.onQuestionTextChange(it) },
                            text = state.questionText
                        )
                        TextFieldWithBubble(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Question answer",
                            text = currentState.currentAnswer,
                            answers = currentState.answers,
                            onDone = { viewModel.onQuestionAnswerAdd(it) },
                            onChange = { viewModel.onQuestionAnswerChange(it) },
                            onDeleteClick = { viewModel.onDeleteAnswerClick(it) }
                        )
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Question points",
                            onChange = { viewModel.onQuestionPointsChange(it) },
                            text = state.points.toString(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Number
                            )
                        )
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Question duration",
                            onChange = { viewModel.onQuestionDurationChange(it) },
                            text = state.duration.toString(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Number
                            )
                        )
                        Image(
                            modifier = Modifier.size(100.dp),
                            painter = rememberAsyncImagePainter(state.questionImage),
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
                            painter = rememberAsyncImagePainter(state.answerImage, onState = {
                                Log.d("MYTAG", "onState: $it")
                            }),
                            contentDescription = "My Image"
                        )
                        Button(onClick = {
                            answerImageLauncher.launch("image/*")
                        }) {
                            val text = "Answer image"
                            Text(text = text)
                        }
                        Button(onClick = {
                            viewModel.onCreateQuestionClick {
                                navController.popBackStack()
                            }
                        }) {
                            val text = if (state.isUpdatingQuestion) {
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
