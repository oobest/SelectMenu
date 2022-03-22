package com.albert.selectmenu;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author albert
 * @date 2022/1/20 14:55
 * @mail 416392758@@gmail.com
 * @since v1
 */
public class DataItem implements Parcelable {

    private int index;

    private String text;

    public int getIndex() {
        return index;
    }

    public String getText() {
        return text;
    }

    public DataItem(int index, String text) {
        this.index = index;
        this.text = text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.index);
        dest.writeString(this.text);
    }

    public void readFromParcel(Parcel source) {
        this.index = source.readInt();
        this.text = source.readString();
    }

    public DataItem() {
    }

    protected DataItem(Parcel in) {
        this.index = in.readInt();
        this.text = in.readString();
    }

    public static final Creator<DataItem> CREATOR = new Creator<DataItem>() {
        @Override
        public DataItem createFromParcel(Parcel source) {
            return new DataItem(source);
        }

        @Override
        public DataItem[] newArray(int size) {
            return new DataItem[size];
        }
    };
}
