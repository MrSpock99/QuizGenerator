package apps.robot.quizgenerator

import android.content.Context
import apps.robot.quizgenerator.createquiz.main.presentation.QuizInfoViewModel
import apps.robot.quizgenerator.createquiz.openquestion.CreateOpenQuestionViewModel
import apps.robot.quizgenerator.createquiz.questionwithoptions.CreateQuestionWithOptionsViewModel
import apps.robot.quizgenerator.data.QuizRepositoryImpl
import apps.robot.quizgenerator.domain.QuizRepository
import apps.robot.quizgenerator.quizlist.presentation.QuizListViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun appModule() = module {
    factory { androidContext().getSharedPreferences("quiz_generator", Context.MODE_PRIVATE) }

    single { FirebaseFirestore.getInstance() }

    factory<QuizRepository> { QuizRepositoryImpl(get(), androidContext()) }

    viewModel {
        QuizListViewModel(get())
    }
    viewModel {
        QuizInfoViewModel(repository = get(), context = androidApplication())
    }
    viewModel {
        CreateOpenQuestionViewModel(repository = get())
    }
    viewModel {
        CreateQuestionWithOptionsViewModel(repository = get())
    }
}