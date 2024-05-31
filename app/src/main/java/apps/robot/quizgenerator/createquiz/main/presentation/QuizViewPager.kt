package apps.robot.quizgenerator.createquiz.main.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import apps.robot.quizgenerator.createquiz.questionlist.QuizQuestionList
import kotlinx.coroutines.launch

@Composable
fun QuizViewPager(quizId: String?, navController: NavHostController) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    Scaffold {
        Surface(
            modifier = Modifier.padding(it)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Tabs
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions.get(pagerState.currentPage))
                        )
                    }
                ) {
                    Tab(
                        selected = pagerState.currentPage == 0,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        },
                        text = { Text(text = "Create Quiz") }
                    )
                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        },
                        text = { Text(text = "Question list") }
                    )
                }

                // Viewpager
                HorizontalPager(state = pagerState) { page ->
                    when (page) {
                        0 -> QuizInfo(quizId =quizId)
                        1 -> QuizQuestionList(quizId, navController =navController)
                    }
                }
            }
        }
    }
}