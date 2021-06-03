package me.showang.transtate.example.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.showang.transtate.TranstateViewModel
import me.showang.transtate.core.Transform
import me.showang.transtate.core.ViewEvent
import me.showang.transtate.core.ViewState
import me.showang.transtate.example.model.ItemData
import me.showang.transtate.example.repository.ItemRepository

class ExampleViewModel(private val repository: ItemRepository) :
    TranstateViewModel<ExampleViewState>(), ItemRepository.Listener {
    override val initState: ExampleViewState = ExampleViewState.Initializing
    override var lastState: ExampleViewState = initState

    init {
        repository.addListener(this)
        loadPageStartAt(0)
    }

    fun loadPageStartAt(index: Int) = viewModelScope.launch(IO) {
        delay(2000)
        if (isActive) repository.loadPage(index)
    }

    override fun onItemInserted(atPosition: Int, newItem: ItemData) {
        startTransform(ExampleViewEvent.InsertNewItem(atPosition, newItem))
    }

    override fun onPageLoaded(
        startPosition: Int,
        newItems: List<ItemData>,
        hasNextPage: Boolean
    ) {
        startTransform(ExampleViewEvent.PageLoaded(startPosition, newItems, hasNextPage))
    }

    override fun onCleared() {
        repository.removeListener(this)
    }

    fun insertNewItem() = CoroutineScope(IO).launch {
        repository.insertNewItem()
    }
}

sealed class ExampleViewEvent : ViewEvent() {

    class PageLoaded(val startAt: Int, val data: List<ItemData>, val hasNextPage: Boolean) :
        ExampleViewEvent()

    class InsertNewItem(val atPosition: Int, val data: ItemData) : ExampleViewEvent()

    class RemoveItem(val dataPosition: Int, val uiPosition: Int) : ExampleViewEvent()

}

sealed class ExampleViewState : ViewState() {

    object Initializing : ExampleViewState() {
        override fun transform(
            byEvent: ViewEvent,
            liveData: MutableLiveData<Transform>
        ): ExampleViewState = when (byEvent) {
            is ExampleViewEvent.PageLoaded -> DataLoaded(byEvent.data, byEvent.hasNextPage)
            else -> Initializing
        }
    }

    class DataLoaded(val itemList: List<ItemData>, val hasNextPage: Boolean) : ExampleViewState() {
        override fun transform(
            byEvent: ViewEvent,
            liveData: MutableLiveData<Transform>
        ): ExampleViewState = when (byEvent) {
            is ExampleViewEvent.PageLoaded -> DataLoaded(
                listOf(itemList, byEvent.data).flatten(),
                byEvent.hasNextPage
            )
            is ExampleViewEvent.InsertNewItem -> DataLoaded(itemList.toMutableList().apply {
                add(byEvent.atPosition, byEvent.data)
            }, hasNextPage)
            is ExampleViewEvent.RemoveItem -> DataLoaded(itemList.toMutableList().apply {
                removeAt(byEvent.dataPosition)
            }, hasNextPage)
            else -> this
        }
    }
}
