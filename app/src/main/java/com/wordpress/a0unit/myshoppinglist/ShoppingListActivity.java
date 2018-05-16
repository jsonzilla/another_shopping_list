package com.wordpress.a0unit.myshoppinglist;

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

public class ShoppingListActivity extends AppCompatActivity  {

    private static final String KEY_ITEM = "item";

    private ListView mShoppingList;

    private EditText mItemEdit;
    private Button mAddButton;

    private ArrayAdapter<String> sAdapter;
    private SwipeActionAdapter mAdapter;

    private ArrayList<String> itemsMemory;

    protected void SaveStorageData() {
        Set<String> mItemsStorage = new HashSet<>(itemsMemory);
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_data), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(getString(R.string.shared_data), mItemsStorage);
        editor.apply();
    }

    protected void LoadStorageData() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_data), MODE_PRIVATE);
        Set<String> itemsStorage = sharedPref.getStringSet(getString(R.string.shared_data), null);
        if (itemsStorage != null) {
            itemsMemory = new ArrayList<>(itemsStorage);
        }
    }

    protected void AddItemToViewList(String item) {
        sAdapter.add(item);
        itemsMemory.add(item);
        mAdapter.notifyDataSetChanged();
        mItemEdit.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        mShoppingList = findViewById(R.id.shopping_listView);
        mItemEdit = findViewById(R.id.item_editText);
        mAddButton = findViewById(R.id.add_button);

        sAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mAdapter = new SwipeActionAdapter(sAdapter);
        mAdapter.setListView(mShoppingList);

        mShoppingList.setAdapter(mAdapter);

        mAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.row_bg_left)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT,R.layout.row_bg_left_far)
                .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT,R.layout.row_bg_right)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT,R.layout.row_bg_right);

        if (savedInstanceState == null) {

            itemsMemory = new ArrayList<>();
            LoadStorageData();
        } else {
            itemsMemory = savedInstanceState.getStringArrayList(KEY_ITEM);
        }
        sAdapter.addAll(itemsMemory);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemToViewList(mItemEdit.getText().toString());
            }
        });

        mAdapter.setSwipeActionListener(new SwipeActionListener(){
            @Override
            public boolean hasActions(int position, SwipeDirection direction){
                if(direction.isLeft()) return true;
                if(direction.isRight()) return true;
                return false;
            }

            @Override
            public boolean shouldDismiss(int position, SwipeDirection direction){
                return direction == SwipeDirection.DIRECTION_FAR_RIGHT ||
                       direction == SwipeDirection.DIRECTION_NORMAL_RIGHT ||
                       direction == SwipeDirection.DIRECTION_FAR_LEFT ||
                       direction == SwipeDirection.DIRECTION_NORMAL_LEFT;
            }

            @Override
            public void onSwipe(int[] positionList, SwipeDirection[] directionList){
                for(int i=0; i < positionList.length;i++) {
                    int position = positionList[i];
                    SwipeDirection direction = directionList[i];
                    switch (direction) {
                        case DIRECTION_FAR_LEFT:
                            Undone(position);
                            break;
                        case DIRECTION_NORMAL_LEFT:
                            DoneItem(position);
                            break;
                        case DIRECTION_NORMAL_RIGHT:
                        case DIRECTION_FAR_RIGHT:
                            RemoveItem(position);
                            break;
                    }
                    sAdapter.notifyDataSetChanged();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void Undone(int position) {
        try {
            String item = itemsMemory.get(position);
            itemsMemory.set(position, item.replace(getString(R.string.done_item),""));
        }
        catch (IndexOutOfBoundsException ignored) {}
    }

    private void DoneItem(int position) {
        try {
            String item = itemsMemory.get(position);
            itemsMemory.set(position, getString(R.string.done_item) + item);
        }
        catch (IndexOutOfBoundsException ignored) {}
    }

    private void RemoveItem(int position) {
        try {
            String item = itemsMemory.get(position);
            itemsMemory.remove(item);
            sAdapter.remove(item);
        }
        catch (IndexOutOfBoundsException ignored) {}
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList(KEY_ITEM, itemsMemory);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        itemsMemory = savedInstanceState.getStringArrayList(KEY_ITEM);
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

}
