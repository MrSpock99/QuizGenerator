package apps.robot.quizgenerator.createquiz.questionlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import apps.robot.quizgenerator.R
import apps.robot.quizgenerator.createquiz.main.presentation.QuizInfoViewModel
import apps.robot.quizgenerator.domain.OpenQuestion
import apps.robot.quizgenerator.domain.QuestionModel
import apps.robot.quizgenerator.domain.QuestionWithOptions
import apps.robot.quizgenerator.domain.QuizRound
import apps.robot.quizgenerator.presentation.CreateOpenQuestionScreen
import apps.robot.quizgenerator.presentation.CreateQuestionWithOptionsScreen
import apps.robot.quizgenerator.presentation.CreateRoundScreen

@Composable
fun QuizQuestionList(
    viewModel: QuizInfoViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val openDialog = remember {
        mutableStateOf(false)
    }

   /* LaunchedEffect(key1 = Unit) {
        viewModel.onResume()
    }*/
    if (openDialog.value) {
        CreateQuestionDialog(onCreateOpenQuestionClick = {
            navController.navigate(CreateOpenQuestionScreen(quizId = state.quizId, questionId = null))
        }, onCreateQuestionWithOptionsClick = {
            navController.navigate(CreateQuestionWithOptionsScreen(quizId = state.quizId, questionId = null))
        }, onCreateRoundClick = {
            navController.navigate(CreateRoundScreen(quizId = state.quizId, roundId = null))
        }, onDismissRequest = {
            navController.popBackStack()
        })
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            itemsIndexed(state.list, key = { _, question -> question.id }) { index, question ->
                when (question) {
                    is OpenQuestion -> {
                        OpenQuestionItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            questionModel = question, index = index, onClick = {
                                navController.navigate(
                                    CreateOpenQuestionScreen(
                                        quizId = state.quizId,
                                        questionId = it.id
                                    )
                                )
                            })
                    }

                    is QuestionWithOptions -> {
                        QuestionWithOptionsItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            questionModel = question, index = index, onClick = {
                                navController.navigate(
                                    CreateQuestionWithOptionsScreen(
                                        quizId = state.quizId,
                                        questionId = it.id
                                    )
                                )
                            })
                    }

                    is QuizRound -> {
                        Row(
                            modifier = Modifier.clickable {
                                navController.navigate(
                                    CreateRoundScreen(
                                        quizId = state.quizId,
                                        roundId = question.id
                                    )
                                )
                            }
                        ) {
                            Text(text = index.toString())
                            Text(text = question.title)
                        }
                    }
                }

            }
        }
        FloatingActionButton(
            onClick = {
                openDialog.value = true
            },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add question"
            )
        }
    }

}

@Composable
fun OpenQuestionItem(modifier: Modifier, questionModel: QuestionModel, index: Int, onClick: (QuestionModel) -> Unit) {
    Row(
        modifier = modifier
            .clickable { onClick(questionModel) }, verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp),
            painter = painterResource(id = R.drawable.question_square),
            contentDescription = null
        )
        Column {
            Text(text = "$index.${questionModel.text}")
        }
    }
}

@Composable
fun QuestionWithOptionsItem(
    modifier: Modifier,
    questionModel: QuestionModel,
    index: Int,
    onClick: (QuestionModel) -> Unit
) {
    Row(
        modifier = modifier
            .clickable { onClick(questionModel) }, verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp),
            painter = painterResource(id = R.drawable.quiz_alt),
            contentDescription = null
        )
        Column {
            Text(text = "$index.${questionModel.text}")
        }
    }
}

@Composable
fun CreateQuestionDialog(
    onDismissRequest: () -> Unit,
    onCreateOpenQuestionClick: () -> Unit,
    onCreateQuestionWithOptionsClick: () -> Unit,
    onCreateRoundClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Create question",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Text(
                    text = "Select question type",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onCreateOpenQuestionClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = "Open question")
                }
                Button(
                    onClick = onCreateQuestionWithOptionsClick
                ) {
                    Text(text = "Question with options")
                }
                Button(
                    onClick = onCreateRoundClick
                ) {
                    Text(text = "Question round")
                }
            }
        }
    )
}