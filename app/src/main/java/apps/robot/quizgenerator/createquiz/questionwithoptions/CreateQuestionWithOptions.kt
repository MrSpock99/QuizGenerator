package apps.robot.quizgenerator.createquiz.questionwithoptions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import apps.robot.quizgenerator.createquiz.presentation.CustomTextField
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

    LaunchedEffect(key1 = Unit) {
        viewModel.onReceiveArgs(quizId, questionId)
    }

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
                modifier = Modifier.fillMaxSize()
            ) {
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Question text",
                    onChange = { viewModel.onQuestionTextChange(it) },
                    text = state.text
                )

                state.answers.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onAnswerChecked(index)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = index == state.rightAnswerIndex,
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
                    text = state.points.toString(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    )
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