package me.showang.transtate.example.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import github.showang.kat.assign
import github.showang.kat.assignN
import me.showang.recyct.RecyctAdapter
import me.showang.recyct.RecyctViewHolder
import me.showang.recyct.items.RecyctItemBase
import me.showang.transtate.core.Transform
import me.showang.transtate.core.ViewState
import me.showang.transtate.example.R
import me.showang.transtate.example.databinding.FragmentItemListBinding
import me.showang.transtate.example.model.ItemData
import me.showang.transtate.example.ui.viewmodel.ExampleViewEvent.*
import me.showang.transtate.example.ui.viewmodel.ExampleViewModel
import me.showang.transtate.example.ui.viewmodel.ExampleViewState.DataLoaded
import me.showang.transtate.example.ui.viewmodel.ExampleViewState.Initializing
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ItemListFragment : Fragment() {

    private val viewModel: ExampleViewModel by sharedViewModel()
    private var binding: FragmentItemListBinding? = null
    private lateinit var mLayoutManager: LinearLayoutManager

    private val mItemList = mutableListOf<ItemData>()
    private var adapter: RecyctAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentItemListBinding.inflate(inflater)
        .assignN(::binding).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.run {
            initRecyclerView()
            viewModel.observeTransformation(viewLifecycleOwner, ::initViewBy, ::updateViewBy)
        }
    }

    private fun initViewBy(newState: ViewState) {
        Log.e(javaClass.simpleName, "initViewBy: $newState")
        newState.apply {
            when (this) {
                is Initializing -> binding?.updateInitializing(true)
                is DataLoaded -> displayLoadedComplete()
            }
        }
    }

    private fun updateViewBy(transform: Transform) {
        Log.e(javaClass.simpleName, "updateViewBy: $transform")
        transform.byEvent.run {
            when (this) {
                is PageLoaded -> handlePageLoaded(transform.fromState)
                is InsertNewItem -> handleInsert()
                is RemoveItem -> handleRemove()
            }
        }
    }

    private fun FragmentItemListBinding.updateInitializing(isInit: Boolean) {
        progress.isVisible = isInit
        initTextView.isVisible = isInit
    }

    private fun DataLoaded.displayLoadedComplete() = binding?.run {
        updateInitializing(false)
        mItemList.clear()
        mItemList.addAll(itemList)
        adapter?.enableLoadMore = hasNextPage
        adapter?.notifyDataSetChanged()
    }

    private fun InsertNewItem.handleInsert() {
        mItemList.add(atPosition, data)
        adapter?.notifyItemInserted(atPosition)
        binding?.recycler?.startInsertTopAnimation()
    }

    private fun RemoveItem.handleRemove() {
        mItemList.removeAt(dataPosition)
        adapter?.notifyItemRemoved(uiPosition)
    }

    private fun PageLoaded.handlePageLoaded(fromState: ViewState?) {
        if (fromState is Initializing) {
            binding?.run {
                updateInitializing(false)
            }
        }

        adapter?.run {
            var loadMoreItemOffset = 0
            val preItemCount = itemCount
            mItemList.addAll(data)
            if (enableLoadMore) {
                notifyItemChanged(preItemCount - 1)
                loadMoreItemOffset += 1
            }
            if (hasNextPage) {
                notifyDataAppended(data.size - loadMoreItemOffset)
            }
            enableLoadMore = hasNextPage
        }
    }

    private fun FragmentItemListBinding.initRecyclerView() {
        LinearLayoutManager(context)
            .apply(recycler::setLayoutManager).assign(::mLayoutManager)
        recycler.adapter = RecyctAdapter(mItemList).apply {
            register(ExampleListItem())
            defaultLoadMore { viewModel.loadPageStartAt(mItemList.size) }
        }.assignN(::adapter)
    }

    private fun RecyclerView.startInsertTopAnimation() {
        val firstChildView = findChildViewUnder(0f, 0f)
        val firstItemPosition = firstChildView?.let {
            getChildAdapterPosition(it)
        }?.takeIf { it > 1 } ?: 1
        val offset = firstChildView?.y?.toInt() ?: 0
        mLayoutManager.scrollToPositionWithOffset(
            firstItemPosition - 1,
            offset
        )
    }

    class ExampleListItem : RecyctItemBase() {
        override fun create(inflater: LayoutInflater, parent: ViewGroup): RecyctViewHolder =
            object : RecyctViewHolder(inflater, parent, R.layout.item_example_data) {
                private val titleText: TextView by id(R.id.titleText)

                override fun bind(data: Any, atIndex: Int) {
                    (data as? ItemData)?.let { itemData ->
                        titleText.text = context.getString(
                            R.string.exampleItem_titleText,
                            itemData.id
                        )
                    }
                }
            }
    }
}