package com.example.myapplication.menu;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author albert
 * @date 2022/1/20 16:00
 * @mail 416392758@@gmail.com
 * @since v1
 */
public class SelectMenu<T> {

    private static final String TAG = "SelectMenu";

    private FragmentManager fragmentManager;
    private List<T> dataList;
    private T selectedItem;
    private View bindView;
    private boolean searchable = false;
    private SameItemImpl<T> isSameFunction;

    public SelectMenu<T> setFragmentManager(@NonNull FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        return this;
    }

    public SelectMenu<T> setDataList(@NonNull List<T> dataList) {
        this.dataList = dataList;
        return this;
    }

    public SelectMenu<T> setSelectedItem(T selectedItem) {
        this.selectedItem = selectedItem;
        return this;
    }

    public SelectMenu<T> setBindView(@NonNull View bindView) {
        this.bindView = bindView;
        return this;
    }

    public SelectMenu<T> setSearchable(boolean searchable) {
        this.searchable = searchable;
        return this;
    }

    public SelectMenu<T> setIsSameFunction(SameItemImpl<T> isSameFunction) {
        this.isSameFunction = isSameFunction;
        return this;
    }

    public void show(@NonNull SelectMenuCallback<T> menuCallback) {
        Objects.requireNonNull(fragmentManager, "fragmentManger=null");
        Objects.requireNonNull(bindView, "bindView=null");
        if (dataList == null || dataList.size() == 0) {
            throw new RuntimeException("dataList=null or dataList is empty");
        }
        int width = bindView.getWidth();
        int height = bindView.getHeight();
        int[] location = new int[2];
        bindView.getLocationOnScreen(location);
        ArrayList<DataItem> dataItems = convert(dataList);
        DataItem defaultSelect = computeSelectedDataItem(dataItems);
        SelectMenuDialogInput dialogInput = new SelectMenuDialogInput()
                .setDataItems(dataItems)
                .setSelectedItem(defaultSelect)
                .setSelectViewWidth(width)
                .setSelectViewHeight(height)
                .setXPosition(location[0])
                .setYPosition(location[1])
                .setSearchable(searchable);
        SelectMenuDialog.show(fragmentManager, dialogInput, new SelectMenuDialogCallback() {
            @Override
            public void onCallback(int index, String text) {
                menuCallback.onCallback(dataList.get(index));
            }
        });
    }

    // 获取默认项
    private DataItem computeSelectedDataItem(ArrayList<DataItem> dataItems) {
        if (selectedItem == null) {
            return null;
        }
        if (isSameFunction != null) {
            for (int i = 0; i < dataList.size(); i++) {
                if (isSameFunction.isSame(dataList.get(i), selectedItem)) {
                    return dataItems.get(i);
                }
            }
        } else {
            String text = getText(selectedItem);
            for (DataItem item : dataItems) {
                if (item.getText().equals(text)) {
                    return item;
                }
            }
        }
        return null;
    }

    // 数据转换
    public ArrayList<DataItem> convert(List<T> dataList) {
        if (dataList == null || dataList.size() == 0) {
            return new ArrayList<>();
        } else {
            ArrayList<DataItem> dataItems = new ArrayList<>();
            for (int i = 0; i < dataList.size(); i++) {
                String text = getText(dataList.get(i));
                DataItem dataItem = new DataItem(i, text);
                dataItems.add(dataItem);
            }
            return dataItems;
        }
    }

    private String getText(T t) {
        String text;
        if (t instanceof String) {
            text = (String) t;
        } else if (t instanceof DataTextImpl) {
            text = ((DataTextImpl) t).getText();
        } else {
            text = t.toString();
        }
        return text;
    }
}
