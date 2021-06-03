package me.showang.transtate.core

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

abstract class ViewState(val timestamp: Long = System.currentTimeMillis()) {

    fun startTransform(event: ViewEvent, liveData: MutableLiveData<Transform>): ViewState {
        val preState = this
        return transform(event, liveData).also { newState ->
            CoroutineScope(Main).launch {
                liveData.value = Transform(
                    preState,
                    newState,
                    event
                )
                event.handled()
            }
        }
    }

    protected abstract fun transform(
        byEvent: ViewEvent,
        liveData: MutableLiveData<Transform>
    ): ViewState

    companion object {
        fun empty(): ViewState = EmptyState()
    }
}

class EmptyState : ViewState() {
    override fun transform(byEvent: ViewEvent, liveData: MutableLiveData<Transform>): ViewState {
        return this
    }
}