package me.showang.transtate.example

import android.app.Application
import me.showang.transtate.example.repository.ItemRepository
import me.showang.transtate.example.ui.viewmodel.ExampleViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ExampleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ExampleApp)
            modules(module {
                single { ItemRepository() }
                viewModel { ExampleViewModel(get()) }
            })
        }
    }

}