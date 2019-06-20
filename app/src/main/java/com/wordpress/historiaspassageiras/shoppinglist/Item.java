package com.wordpress.historiaspassageiras.shoppinglist;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class Item implements Parcelable {
    public static final Parcelable.Creator<Item> CREATOR
            = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
    private String item;
    private Boolean done;

    public Item(String i, Boolean d) {
        item = i;
        done = d;
    }

    private Item(Parcel in) {
        item = in.readString();
        done = in.readInt() == 1;
    }

    static public Item create(String seralizedData) {
        Gson gson = new Gson();
        return gson.fromJson(seralizedData, Item.class);
    }

    void SetItem(String i) {
        item = i;
    }

    String GetItem() {
        return item;
    }

    void Done() {
        done = true;
    }

    void Undone() {
        done = false;
    }

    Boolean IsDone() {
        return done;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(item);
        out.writeInt(done ? 0 : 1);
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
