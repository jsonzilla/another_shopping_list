package com.wordpress.historiaspassageiras.shoppinglist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class ItemAdapter extends ArrayAdapter<Item> {

    final int _layoutId;
    private final Context _context;

    public ItemAdapter(Context context, int layoutId) {
        super(context, layoutId);
        _context = context;
        _layoutId = layoutId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Item item = getItem(position);
        String itemName = item.GetItem();
        Boolean done = item.IsDone();
        String doneString = done ? "Done" : "";
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(_layoutId, parent, false);
        }

        TextView itemView = view.findViewById(R.id.itemItem);
        TextView doneView = view.findViewById(R.id.itemDone);
        itemView.setText(itemName);
        doneView.setText(doneString);

        return view;
    }

}
