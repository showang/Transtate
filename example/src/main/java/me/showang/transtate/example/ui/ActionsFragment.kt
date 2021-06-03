package me.showang.transtate.example.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import github.showang.kat.assignN
import me.showang.transtate.example.databinding.FragmentActionsBinding
import me.showang.transtate.example.ui.viewmodel.ExampleViewModel
import me.showang.transtate.example.ui.viewmodel.ExampleViewState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ActionsFragment : Fragment() {

    private val viewModel: ExampleViewModel by sharedViewModel()
    private var binding: FragmentActionsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentActionsBinding.inflate(inflater).assignN(::binding).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.run {
            loadNewPageButton.setOnClickListener {
                val currentState = viewModel.currentState
                if (currentState is ExampleViewState.DataLoaded) {
                    viewModel.loadPageStartAt(currentState.itemList.size)
                }
            }
        }
    }

}