package apps.robot.quizgenerator.createquiz.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@Composable
fun QuizViewPager() {
    val pagerState = rememberPagerState(pageCount = 2)

    Column(modifier = Modifier.fillMaxSize()) {
        // Tabs
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = { pagerState.animateScrollToPage(0) },
                text = { Text(text = "Create Quiz") }
            )
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = { pagerState.animateScrollToPage(1) },
                text = { Text(text = "Create Open Question") }
            )
        }

        // Viewpager
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> CreateQuiz()
                1 -> CreateOpenQuestion()
            }
        }
    }
}