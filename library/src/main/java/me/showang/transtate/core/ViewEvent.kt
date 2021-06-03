package me.showang.transtate.core

abstract class ViewEvent(
    private var mIsHandled: Boolean = false
) {

    val shouldHandle: Boolean
        get() = !mIsHandled

    fun handled() {
        mIsHandled = true
    }

    companion object {
        fun empty(): ViewEvent = EmptyEvent()
    }

}

class EmptyEvent : ViewEvent()