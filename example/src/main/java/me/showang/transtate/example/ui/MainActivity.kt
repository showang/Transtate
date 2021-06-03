package me.showang.transtate.example.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import github.showang.kat.assign
import me.showang.transtate.example.R
import me.showang.transtate.example.databinding.ActivityMainBinding
import me.showang.transtate.example.extention.setupWithNavController
import me.showang.transtate.example.ui.viewmodel.ExampleViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: ExampleViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).apply {
            initBottomNavigation()
            initFab()
        }.assign(::binding).root.let(::setContentView)
    }

    private fun ActivityMainBinding.initBottomNavigation() {
        bottomNavigation.setupWithNavController(
            listOf(R.navigation.nav_item_list, R.navigation.nav_actions),
            supportFragmentManager,
            R.id.navHostFragmentContainer,
            intent
        )
    }

    private fun ActivityMainBinding.initFab() = fab.setOnClickListener {
        viewModel.insertNewItem()
    }
}