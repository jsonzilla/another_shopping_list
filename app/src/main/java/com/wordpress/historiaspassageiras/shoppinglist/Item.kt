package com.wordpress.historiaspassageiras.shoppinglist

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.Gson

class Item : Parcelable {
    private var item: String = ""
    private var itemDone: Boolean = false

    constructor(i: String, d: Boolean) {
        item = i
        itemDone = d
    }

    private constructor(`in`: Parcel) {
        item = `in`.readString()
        itemDone = `in`.readInt() == 1
    }

    internal fun getItem() = item

    internal fun done() {
        itemDone = true
    }

    internal fun undone() {
        itemDone = false
    }

    internal fun isDone() = itemDone

    override fun describeContents() = 0

    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeString(item)
        out.writeInt(if (itemDone) 0 else 1)
    }

    fun serialize(): String = Gson().toJson(this)

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }

        fun create(serializedData: String): Item {
            return  Gson().fromJson(serializedData, Item::class.java)
        }
    }
}