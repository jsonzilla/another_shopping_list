package com.wordpress.historiaspassageiras.shoppinglist

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView

import com.wdullaer.swipeactionadapter.SwipeActionAdapter
import com.wdullaer.swipeactionadapter.SwipeActionAdapter.SwipeActionListener
import com.wdullaer.swipeactionadapter.SwipeDirection
import com.wdullaer.swipeactionadapter.SwipeDirection.*

import java.util.ArrayList
import java.util.HashSet

class ShoppingListActivity : AppCompatActivity() {
    private var itemEdit: EditText? = null
    private var itemsAdapter: ArrayAdapter<Item>? = null
    private var swipeListAdapter: SwipeActionAdapter? = null
    private var itemsMemory: ArrayList<Item>? = null

    private fun saveStorageData() {
        val serialized = HashSet<String>()
        HashSet(itemsMemory!!).forEach { i ->
            serialized.add(i.serialize())
        }

        val editor = getSharedPreferences(getString(R.string.shared_data), Context.MODE_PRIVATE).edit()
        editor.putStringSet(getString(R.string.shared_data), serialized)
        editor.apply()
    }

    private fun loadStorageData() {
        val sharedPref = getSharedPreferences(getString(R.string.shared_data), Context.MODE_PRIVATE)
        val serialized = sharedPref.getStringSet(getString(R.string.shared_data), null)
        if (serialized != null) {
            val itemsStorage = serialized
                    .filter { it.isNotEmpty() }
                    .map { Item.create(it) }
                    .toSet()

            if (itemsStorage.isNotEmpty()) {
                itemsMemory = ArrayList(itemsStorage)
            }
        }
    }

    private fun addItemToViewList(item: String) {
        if (item.isNotEmpty()) {
            val i = Item(item, false)
            itemsAdapter!!.add(i)
            itemsMemory!!.add(i)
            swipeListAdapter!!.notifyDataSetChanged()
            itemEdit!!.setText("")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)

        itemEdit = findViewById(R.id.item_editText)
        itemsAdapter = ItemAdapter(this, R.layout.items_list)

        if (savedInstanceState != null) {
            itemsMemory = savedInstanceState.getParcelableArrayList(KEY_ITEM)
        } else {
            itemsMemory = ArrayList()
            loadStorageData()
        }

        itemsAdapter!!.addAll(itemsMemory!!)
        swipeListAdapter = SwipeActionAdapter(itemsAdapter)

        configureSwipe()

        val addButton: Button = findViewById(R.id.add_button)
        addButton.setOnClickListener{ addItemToViewList(itemEdit!!.text.toString()) }
    }

    private fun configureSwipe() {
        val shoppingList: ListView = findViewById(R.id.shopping_listView)
        with(swipeListAdapter!!) {
            setListView(shoppingList)

            addBackground(DIRECTION_FAR_RIGHT, R.layout.row_bg_turn_rigtht)
                .addBackground(DIRECTION_NORMAL_RIGHT, R.layout.row_bg_turn_rigth_far)
                .addBackground(DIRECTION_FAR_LEFT, R.layout.row_bg_turn_left)
                .addBackground(DIRECTION_NORMAL_LEFT, R.layout.row_bg_turn_left)

            setSwipeActionListener(
                    object : SwipeActionListener {
                        override fun hasActions(position: Int, direction: SwipeDirection): Boolean {
                            return if (direction.isLeft) true else direction.isRight
                        }

                        override fun shouldDismiss(position: Int, direction: SwipeDirection): Boolean {
                            return direction != DIRECTION_NEUTRAL
                        }

                        override fun onSwipe(positionList: IntArray, directionList: Array<SwipeDirection>) {
                            positionList.indices.forEach { i ->
                                val position = positionList[i]
                                when (directionList[i]) {
                                    DIRECTION_FAR_RIGHT -> undone(position)
                                    DIRECTION_NORMAL_RIGHT -> doneItem(position)
                                    DIRECTION_FAR_LEFT, DIRECTION_NORMAL_LEFT -> removeItem(position)
                                    DIRECTION_NEUTRAL -> Unit
                                }
                                itemsAdapter!!.notifyDataSetChanged()
                                swipeListAdapter!!.notifyDataSetChanged()
                            }
                        }
                    })
        }
        shoppingList.adapter = swipeListAdapter
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putParcelableArrayList(KEY_ITEM, itemsMemory)
    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        itemsMemory = savedInstanceState.getParcelableArrayList(KEY_ITEM)
    }

    override fun onPause() {
        super.onPause()
        saveStorageData()
    }

    override fun onStop() {
        super.onStop()
        saveStorageData()
    }

    private fun undone(position: Int) {
        try {
            itemsMemory!![position].undone()
        } catch (ignored: IndexOutOfBoundsException) {
        }

    }

    private fun doneItem(position: Int) {
        try {
            itemsMemory!![position].done()
        } catch (ignored: IndexOutOfBoundsException) {
        }

    }

    private fun removeItem(position: Int) {
        try {
            val item = itemsMemory!![position]
            itemsMemory!!.remove(item)
            itemsAdapter!!.remove(item)
        } catch (ignored: IndexOutOfBoundsException) {
        }

    }

    companion object {
        private const val KEY_ITEM = "item"
    }

}
