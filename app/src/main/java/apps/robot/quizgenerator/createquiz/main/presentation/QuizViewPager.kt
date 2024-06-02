package apps.robot.quizgenerator.createquiz.main.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import apps.robot.quizgenerator.R
import apps.robot.quizgenerator.createquiz.questionlist.QuizQuestionList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizViewPager(quizId: String?, navController: NavHostController) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Quiz Info")
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