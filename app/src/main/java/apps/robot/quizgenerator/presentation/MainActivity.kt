package apps.robot.quizgenerator.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import apps.robot.quizgenerator.createquiz.main.presentation.QuizViewPager
import apps.robot.quizgenerator.createquiz.openquestion.CreateOpenQuestion
import apps.robot.quizgenerator.createquiz.questionwithoptions.CreateQuestionWithOptions
import apps.robot.quizgenerator.createquiz.round.CreateQuizRoundComposable
import apps.robot.quizgenerator.quizlist.presentation.QuizList
import apps.robot.quizgenerator.ui.theme.QuizGeneratorTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // Handle the result of the permission request
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                // Process the result for each permission
            }
        }

    private fun requestMediaPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )

        if (requiredPermissions.all {
                ContextCompat.checkSelfPermission(
                    this,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            // Permissions are already granted, proceed with accessing media
        } else {
            requestPermissionLauncher.launch(requiredPermissions)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            requestMediaPermissions()
            QuizGeneratorTheme {
                NavHost(navController = navController, startDestination = QuizListScreen) {
                    composable<QuizListScreen> {
                        QuizList(navController = navController)
                    }
                    composable<CreateOpenQuestionScreen> {
                        val args = it.toRoute<CreateOpenQuestionScreen>()
                        CreateOpenQuestion(
                            quizId = args.quizId,
                            questionId = args.questionId,
                            navController = navController
                        )
                    }
                    composable<CreateQuestionWithOptionsScreen> {
                        val args = it.toRoute<CreateQuestionWithOptionsScreen>()
                        CreateQuestionWithOptions(
                            quizId = args.quizId,
                            questionId = args.questionId,
                            navController = navController
                        )
                    }
                    composable<CreateQuizViewPagerScreen> {
                        val args = it.toRoute<CreateQuizViewPagerScreen>()
                        QuizViewPager(quizId = args.quizId, navController = navController)
                    }
                    composable<CreateRoundScreen> {
                        val args = it.toRoute<CreateRoundScreen>()
                        CreateQuizRoundComposable(
                            quizId = args.quizId,
                            roundId = args.roundId,
                            navController = navController
                        )
                    }
                }
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
data class CreateQuestionWithOptionsScreen(val quizId: String, val questionId: String?)

@Serializable
data class CreateQuizViewPagerScreen(val quizId: String?)

@Serializable
data class CreateRoundScreen(val quizId: String, val roundId: String?)