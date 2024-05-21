package apps.robot.quizgenerator

import android.content.Context
import android.content.SharedPreferences
import apps.robot.quizgenerator.data.QuizRepositoryImpl
import apps.robot.quizgenerator.domain.QuizRepository
import apps.robot.quizgenerator.quizlist.presentation.QuizListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun appModule() = module {
    factory { androidContext().getSharedPreferences("quiz_generator", Context.MODE_PRIVATE) }
    factory<QuizRepository> { QuizRepositoryImpl(get()) }
    viewModel {
        QuizListViewModel(get())
    }
}