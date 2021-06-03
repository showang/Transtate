package me.showang.transtate

import androidx.lifecycle.*
import me.showang.transtate.core.Transform
import me.showang.transtate.core.ViewEvent
import me.showang.transtate.core.ViewState

abstract class TranstateViewModel<STATE : ViewState> : ViewModel() {

    protected abstract val initState: STATE
    protected abstract var lastState: STATE
    private val mTransformLiveData: MutableLiveData<Transform> by lazy {
        MutableLiveData(Transform(ViewState.empty(), initState, ViewEvent.empty()))
    }

    val currentState get() = lastState
    private val transformLiveData: LiveData<Transform> get() = mTransformLiveData

    protected fun startTransform(event: ViewEvent) {
        val newState = currentState.startTransform(event, mTransformLiveData)
        @Suppress("UNCHECKED_CAST")
        lastState = newState as? STATE
            ?: throw IllegalStateException("Unsupported state type")
    }

    fun observeTransformation(
        lifecycleOwner: LifecycleOwner,
        initViewByState: (ViewState) -> Unit,
        updateViewByTransform: (Transform) -> Unit
    ) {
        transformLiveData.observe(lifecycleOwner, Observer { transform ->
            transform.run {
                if (shouldHandleEvent) {
                    updateViewByTransform(this)
                } else {
                    initViewByState(newState)
                }
            }
        })
    }

    fun removeObservers(lifecycleOwner: LifecycleOwner) {
        transformLiveData.removeObservers(lifecycleOwner)
    }
}