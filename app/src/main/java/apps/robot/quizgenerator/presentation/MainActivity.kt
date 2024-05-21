package apps.robot.quizgenerator.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import apps.robot.quizgenerator.ui.theme.QuizGeneratorTheme
import kotlinx.serialization.Serializable
import androidx.navigation.compose.composable
import apps.robot.quizgenerator.createquiz.presentation.CreateOpenQuestion
import apps.robot.quizgenerator.quizlist.presentation.QuizList

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            QuizGeneratorTheme {
                NavHost(navController = navController, startDestination = QuizListScreen) {
                    composable<QuizListScreen> {
                        QuizList()
                    }
                    composable<CreateOpenQuestionScreen> {
                        CreateOpenQuestion()
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
object CreateOpenQuestionScreen