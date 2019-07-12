package com.wordpress.historiaspassageiras.shoppinglist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

internal class ItemAdapter(private val _context: Context, private val _layoutId: Int) : ArrayAdapter<Item>(_context, _layoutId) {

    private fun buildView(position: Int, view: View): View {
        val item = getItem(position)
        val itemView: TextView = view.findViewById(R.id.itemItem)
        val doneView: TextView = view.findViewById(R.id.itemDone)
        itemView.text = item!!.getItem()
        doneView.text  = if (item.isDone()) "Done" else ""

        return view
    }

    override fun getView(position: Int, _view: View?, parent: ViewGroup): View {
        var view = _view
        if (view == null) {
            val inflater = _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(_layoutId, parent, false)
        }

        return buildView(position, view!!)
    }

}
