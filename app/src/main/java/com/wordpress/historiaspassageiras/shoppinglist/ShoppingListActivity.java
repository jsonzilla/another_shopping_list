package com.wordpress.historiaspassageiras.shoppinglist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter.SwipeActionListener;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ShoppingListActivity extends AppCompatActivity {
    private static final String KEY_ITEM = "item";
    private EditText itemEdit;
    private ArrayAdapter<Item> itemsAdapter;
    private SwipeActionAdapter swipeListAdapter;
    private ArrayList<Item> itemsMemory;

    private void SaveStorageData() {
        Set<Item> itemsStorage = new HashSet<>(itemsMemory);
        Set<String> serialized = new HashSet<>();
        for (Item i : itemsStorage) {
            serialized.add(i.serialize());
        }
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_data), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(getString(R.string.shared_data), serialized);
        editor.apply();
    }

    private void LoadStorageData() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_data), MODE_PRIVATE);
        Set<String> serialized = sharedPref.getStringSet(getString(R.string.shared_data), null);
        if (serialized != null) {
            Set<Item> itemsStorage = new HashSet<>();
            for (String s : serialized) {
                if (!s.isEmpty()) {
                    itemsStorage.add(Item.create(s));
                }
            }

            if (itemsStorage.size() != 0) {
                itemsMemory = new ArrayList<>(itemsStorage);
            }
        }
    }

    private void AddItemToViewList(String item) {
        if (!item.isEmpty()) {
            Item i = new Item(item, false);
            itemsAdapter.add(i);
            itemsMemory.add(i);
            swipeListAdapter.notifyDataSetChanged();
            itemEdit.setText("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        itemEdit = findViewById(R.id.item_editText);
        itemsAdapter = new ItemAdapter(this, R.layout.items_list);

        if (savedInstanceState != null) {
            itemsMemory = savedInstanceState.getParcelableArrayList(KEY_ITEM);
        } else {
            itemsMemory = new ArrayList<>();
            LoadStorageData();
        }

        itemsAdapter.addAll(itemsMemory);
        swipeListAdapter = new SwipeActionAdapter(itemsAdapter);

        ConfigureSwipe();

        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemToViewList(itemEdit.getText().toString());
            }
        });
    }

    private void ConfigureSwipe() {
        ListView shoppingList = findViewById(R.id.shopping_listView);
        swipeListAdapter.setListView(shoppingList);
        swipeListAdapter.addBackground(SwipeDirection.DIRECTION_FAR_RIGHT, R.layout.row_bg_turn_rigtht)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_turn_rigth_far)
                .addBackground(SwipeDirection.DIRECTION_FAR_LEFT, R.layout.row_bg_turn_left)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.row_bg_turn_left);

        swipeListAdapter.setSwipeActionListener(new SwipeActionListener() {
            @Override
            public boolean hasActions(int position, SwipeDirection direction) {
                if (direction.isLeft()) return true;
                return direction.isRight();
            }

            @Override
            public boolean shouldDismiss(int position, SwipeDirection direction) {
                return direction == SwipeDirection.DIRECTION_FAR_RIGHT ||
                        direction == SwipeDirection.DIRECTION_NORMAL_RIGHT ||
                        direction == SwipeDirection.DIRECTION_FAR_LEFT ||
                        direction == SwipeDirection.DIRECTION_NORMAL_LEFT;
            }

            @Override
            public void onSwipe(int[] positionList, SwipeDirection[] directionList) {
                for (int i = 0; i < positionList.length; i++) {
                    int position = positionList[i];
                    SwipeDirection direction = directionList[i];
                    switch (direction) {
                        case DIRECTION_FAR_RIGHT:
                            Undone(position);
                            break;
                        case DIRECTION_NORMAL_RIGHT:
                            DoneItem(position);
                            break;
                        case DIRECTION_FAR_LEFT:
                        case DIRECTION_NORMAL_LEFT:
                            RemoveItem(position);
                            break;
                    }
                    itemsAdapter.notifyDataSetChanged();
                    swipeListAdapter.notifyDataSetChanged();
                }
            }
        });
        shoppingList.setAdapter(swipeListAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(KEY_ITEM, itemsMemory);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        itemsMemory = savedInstanceState.getParcelableArrayList(KEY_ITEM);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SaveStorageData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SaveStorageData();
    }

    private void Undone(int position) {
        try {
            itemsMemory.get(position).Undone();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    private void DoneItem(int position) {
        try {
            itemsMemory.get(position).Done();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    private void RemoveItem(int position) {
        try {
            Item item = itemsMemory.get(position);
            itemsMemory.remove(item);
            itemsAdapter.remove(item);
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

}
