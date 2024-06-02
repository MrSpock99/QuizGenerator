package apps.robot.quizgenerator.createquiz.openquestion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import apps.robot.quizgenerator.R
import apps.robot.quizgenerator.createquiz.presentation.CustomTextField
import apps.robot.quizgenerator.createquiz.presentation.TextFieldWithBubble
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOpenQuestion(
    quizId: String,
    questionId: String?,
    navController: NavController,
    viewModel: CreateOpenQuestionViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.onReceiveArgs(quizId, questionId)
    }
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
                    text = state.currentAnswer,
                    answers = state.answers,
                    onDone = { viewModel.onQuestionAnswerAdd(it) },
                    onChange = { viewModel.onQuestionAnswerChange(it) },
                    onDeleteClick = { viewModel.onDeleteAnswerClick(it) }
                )
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
