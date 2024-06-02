package apps.robot.quizgenerator.createquiz.questionlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import apps.robot.quizgenerator.domain.QuestionModel
import apps.robot.quizgenerator.presentation.CreateOpenQuestionScreen
import org.koin.androidx.compose.getViewModel

@Composable
fun QuizQuestionList(quizId: String?, viewModel: QuizQuestionListViewModel = getViewModel(), navController: NavController) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.onReceiveArgs(quizId)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            itemsIndexed(state.list, key = { _, question -> question.id }) { index, question ->
                OpenQuestionItem(modifier = Modifier, questionModel = question, onClick = {
                    navController.navigate(CreateOpenQuestionScreen(quizId = state.quizId, questionId = it.id))
                })
            }
        }
        FloatingActionButton(
            onClick = { navController.navigate(CreateOpenQuestionScreen(quizId = state.quizId, questionId = null)) },
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
fun OpenQuestionItem(modifier: Modifier, questionModel: QuestionModel, onClick: (QuestionModel) -> Unit) {
    Row(modifier = modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .clickable { onClick(questionModel) }) {

        Column {
            Text(text = questionModel.title)
            Text(text = questionModel.text)
        }
    }
}