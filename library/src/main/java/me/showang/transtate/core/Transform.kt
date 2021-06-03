package me.showang.transtate.core

class Transform(val fromState: ViewState, val newState: ViewState, val byEvent: ViewEvent) {

    val shouldHandleEvent: Boolean get() = byEvent !is EmptyEvent && byEvent.shouldHandle

    init {
        if (byEvent !is EmptyEvent && !byEvent.shouldHandle) throw IllegalArgumentException("Event should not be handled before sand.")
    }

}