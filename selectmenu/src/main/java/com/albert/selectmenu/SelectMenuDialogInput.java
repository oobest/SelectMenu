package com.albert.selectmenu;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author albert
 * @date 2022/1/20 15:04
 * @mail 416392758@@gmail.com
 * @since v1
 */
public class SelectMenuDialogInput implements Parcelable {

    // 数据列表
    private List<DataItem> dataItems;

    // 选中的数据
    private DataItem selectedItem;

    // 选择器宽度
    private int selectViewWidth;

    // 选择器高度
    private int selectViewHeight;

    // 选择器X坐标
    private int xPosition;

    // 选择器Y坐标
    private int yPosition;

    // 是否显示搜索按钮
    private boolean searchable;

    private String searchInputHint;

    public SelectMenuDialogInput setDataItems(List<DataItem> dataItems) {
        this.dataItems = dataItems;
        return this;
    }

    public SelectMenuDialogInput setSelectedItem(DataItem selectedItem) {
        this.selectedItem = selectedItem;
        return this;
    }

    public SelectMenuDialogInput setSelectViewWidth(int selectViewWidth) {
        this.selectViewWidth = selectViewWidth;
        return this;
    }

    public SelectMenuDialogInput setSelectViewHeight(int selectViewHeight) {
        this.selectViewHeight = selectViewHeight;
        return this;
    }

    public SelectMenuDialogInput setXPosition(int xPosition) {
        this.xPosition = xPosition;
        return this;
    }

    public SelectMenuDialogInput setYPosition(int yPosition) {
        this.yPosition = yPosition;
        return this;
    }

    public SelectMenuDialogInput setSearchable(boolean searchable) {
        this.searchable = searchable;
        return this;
    }

    public SelectMenuDialogInput setSearchInputHint(String searchInputHint){
        this.searchInputHint = searchInputHint;
        return this;
    }

    public List<DataItem> getDataItems() {
        return dataItems;
    }

    public DataItem getSelectedItem() {
        return selectedItem;
    }

    public int getSelectViewWidth() {
        return selectViewWidth;
    }

    public int getSelectViewHeight() {
        return selectViewHeight;
    }

    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public String getSearchInputHint() {
        return searchInputHint;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.dataItems);
        dest.writeParcelable(this.selectedItem, flags);
        dest.writeInt(this.selectViewWidth);
        dest.writeInt(this.selectViewHeight);
        dest.writeInt(this.xPosition);
        dest.writeInt(this.yPosition);
        dest.writeByte(this.searchable ? (byte) 1 : (byte) 0);
        dest.writeString(this.searchInputHint);
    }

    public void readFromParcel(Parcel source) {
        this.dataItems = source.createTypedArrayList(DataItem.CREATOR);
        this.selectedItem = source.readParcelable(DataItem.class.getClassLoader());
        this.selectViewWidth = source.readInt();
        this.selectViewHeight = source.readInt();
        this.xPosition = source.readInt();
        this.yPosition = source.readInt();
        this.searchable = source.readByte() != 0;
        this.searchInputHint = source.readString();
    }

    public SelectMenuDialogInput() {
    }

    protected SelectMenuDialogInput(Parcel in) {
        this.dataItems = in.createTypedArrayList(DataItem.CREATOR);
        this.selectedItem = in.readParcelable(DataItem.class.getClassLoader());
        this.selectViewWidth = in.readInt();
        this.selectViewHeight = in.readInt();
        this.xPosition = in.readInt();
        this.yPosition = in.readInt();
        this.searchable = in.readByte() != 0;
        this.searchInputHint = in.readString();
    }

    public static final Creator<SelectMenuDialogInput> CREATOR = new Creator<SelectMenuDialogInput>() {
        @Override
        public SelectMenuDialogInput createFromParcel(Parcel source) {
            return new SelectMenuDialogInput(source);
        }

        @Override
        public SelectMenuDialogInput[] newArray(int size) {
            return new SelectMenuDialogInput[size];
        }
    };
}
