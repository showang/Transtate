package me.showang.transtate.example.repository

import me.showang.transtate.example.model.ItemData

class ItemRepository {

    companion object {
        private const val SIZE_PER_PAGE = 15
        private const val MAX_PAGE_SIZE = 5 * SIZE_PER_PAGE
    }

    private var increasingId = 0
        get() {
            return field.apply {
                field++
            }
        }
    private val listeners = mutableListOf<Listener>()


    fun insertNewItem(atPosition: Int = 0) {
        val newItem = createNewItem(increasingId)
        listeners.forEachSelf {
            onItemInserted(atPosition, newItem)
        }
    }

    fun loadPage(startPosition: Int) {
        val newItems = (0 until SIZE_PER_PAGE).map { createNewItem(increasingId) }
        listeners.forEachSelf {
            onPageLoaded(startPosition, newItems, (startPosition + SIZE_PER_PAGE) < MAX_PAGE_SIZE)
        }
    }

    private fun createNewItem(increasingId: Int): ItemData {
        return ItemData(increasingId.toString())
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    interface Listener {
        fun onItemInserted(atPosition: Int, newItem: ItemData)
        fun onPageLoaded(startPosition: Int, newItems: List<ItemData>, hasNextPage: Boolean)
    }

}

private inline fun <T> Iterable<T>.forEachSelf(action: T.() -> Unit) {
    for (element in this) action(element)
}