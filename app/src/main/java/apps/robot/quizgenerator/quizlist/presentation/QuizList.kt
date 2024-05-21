package apps.robot.quizgenerator.quizlist.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.androidx.compose.getViewModel

@Composable
fun QuizList(viewModel: QuizListViewModel = getViewModel()) {
    when (val state = viewModel.state.collectAsState().value) {
        QuizListViewModel.QuizListUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        is QuizListViewModel.QuizListUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Что-то пошло не так")
            }
        }

        is QuizListViewModel.QuizListUiState.Success -> {
            SuccessQuizList(success = state, onAddQuizClick = {

            })
        }
    }

}

@Composable
fun SuccessQuizList(success: QuizListViewModel.QuizListUiState.Success, onAddQuizClick: () -> Unit) {
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            state = listState
        ) {
            itemsIndexed(success.quizList, key = { _, quiz -> quiz.id }) { index, quiz ->
                QuizItem(modifier = Modifier, name = quiz.name)
            }
        }

        FloatingActionButton(
            onClick = { onAddQuizClick() },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Quiz"
            )
        }
    }
}

@Composable
fun QuizItem(modifier: Modifier, name: String) {
    Row(
        modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text(text = name)
    }
}