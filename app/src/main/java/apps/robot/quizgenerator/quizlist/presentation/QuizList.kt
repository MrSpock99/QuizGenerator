package apps.robot.quizgenerator.quizlist.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import apps.robot.quizgenerator.domain.QuizModel
import apps.robot.quizgenerator.presentation.CreateQuizViewPagerScreen
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizList(viewModel: QuizListViewModel = getViewModel(), navController: NavController) {

    LaunchedEffect(key1 = Unit) {
        viewModel.onResume()

    }
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Quiz list")
            })
        }
    ) {
        Surface(modifier = Modifier.padding(it)) {
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
                        navController.navigate(CreateQuizViewPagerScreen(null))
                    }, onQuizClick = {
                        navController.navigate(CreateQuizViewPagerScreen(it))
                    })
                }
            }
        }

    }


}

@Composable
fun SuccessQuizList(success: QuizListViewModel.QuizListUiState.Success, onAddQuizClick: () -> Unit, onQuizClick: (String) -> (Unit)) {
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            state = listState
        ) {
            itemsIndexed(success.quizList, key = { _, quiz -> quiz.id }) { index, quiz ->
                QuizItem(modifier = Modifier, item = quiz, onClick = onQuizClick)
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
fun QuizItem(modifier: Modifier, item: QuizModel, onClick: (String) -> Unit) {
    Row(
        modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                onClick(item.id)
            }
    ) {
        Text(text = item.name, fontSize = 32.sp)
    }
}