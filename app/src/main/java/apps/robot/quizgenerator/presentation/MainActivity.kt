package apps.robot.quizgenerator.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import apps.robot.quizgenerator.createquiz.main.presentation.QuizViewPager
import apps.robot.quizgenerator.createquiz.openquestion.CreateOpenQuestion
import apps.robot.quizgenerator.createquiz.questionlist.QuizQuestionList
import apps.robot.quizgenerator.quizlist.presentation.QuizList
import apps.robot.quizgenerator.ui.theme.QuizGeneratorTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            QuizGeneratorTheme {
                NavHost(navController = navController, startDestination = QuizListScreen) {
                    composable<QuizListScreen> {
                        QuizList(navController=navController)
                    }
                    composable<CreateOpenQuestionScreen> {
                        val args = it.toRoute<CreateOpenQuestionScreen>()
                        CreateOpenQuestion(quizId = args.quizId, questionId = args.questionId, navController = navController)
                    }
                    composable<CreateQuizViewPagerScreen> {
                        val args = it.toRoute<CreateQuizViewPagerScreen>()
                        QuizViewPager(quizId = args.quizId, navController =navController)
                    }
                    composable<QuizQuestionListScreen> {
                        val args = it.toRoute<QuizQuestionListScreen>()
                        QuizQuestionList(quizId = args.quizId, navController = navController)
                    }
                }

                /*Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }*/
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuizGeneratorTheme {
        Greeting("Android")
    }
}

@Serializable
object QuizListScreen

@Serializable
data class CreateOpenQuestionScreen(val quizId: String, val questionId: String?)

@Serializable
data class CreateQuizViewPagerScreen(val quizId: String?)

@Serializable
data class QuizQuestionListScreen(val quizId: String?)