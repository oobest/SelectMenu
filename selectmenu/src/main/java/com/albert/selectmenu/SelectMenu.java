package com.albert.selectmenu;


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
    private Integer minWidth;
    private boolean searchable = false;
    private String searchInputHint;
    private SameItemImpl<T> isSameFunction;

    private ItemTextMapper<T> itemTextMapper;

    /**
     * @param fragmentManager
     * @param bindView        绑定View
     * @param dataList        列表数据
     * @return
     */
    public static <Data> SelectMenu<Data> create(@NonNull FragmentManager fragmentManager,
                                                 @NonNull View bindView,
                                                 @NonNull List<Data> dataList) {
        return new SelectMenu<>(fragmentManager, bindView, dataList);
    }

    private SelectMenu(@NonNull FragmentManager fragmentManager, @NonNull View bindView, @NonNull List<T> dataList) {
        this.fragmentManager = fragmentManager;
        this.bindView = bindView;
        this.dataList = dataList;
    }

    /**
     * @param selectedItem 默认选中内容
     */
    public SelectMenu<T> setSelectedItem(T selectedItem) {
        this.selectedItem = selectedItem;
        return this;
    }

    /**
     * @param searchable 是否显示过滤搜索框
     */
    public SelectMenu<T> setSearchable(boolean searchable) {
        this.searchable = searchable;
        return this;
    }

    /**
     * @param isSameFunction 两个元素是否相同判断
     */
    public SelectMenu<T> setIsSameFunction(SameItemImpl<T> isSameFunction) {
        this.isSameFunction = isSameFunction;
        return this;
    }

    /**
     * 显示的Text内容
     *
     * @param itemTextMapper
     * @return 元素的显示内容方法回调
     */
    public SelectMenu<T> setItemTextMapper(ItemTextMapper<T> itemTextMapper) {
        this.itemTextMapper = itemTextMapper;
        return this;
    }

    public SelectMenu<T> setSearchInputHint(String searchInputHint) {
        this.searchInputHint = searchInputHint;
        return this;
    }

    /**
     * 设置最小宽度
     *
     * @param minWidth
     * @return
     */
    public SelectMenu<T> setMinWidth(Integer minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    /**
     * 显示下拉菜单
     *
     * @param menuCallback 选择结果回调函数
     */
    public void show(@NonNull SelectMenuCallback<T> menuCallback) {
        Objects.requireNonNull(fragmentManager, "fragmentManger=null");
        Objects.requireNonNull(bindView, "bindView=null");
        if (dataList == null || dataList.size() == 0) {
            throw new RuntimeException("dataList=null or dataList is empty");
        }
        int width = bindView.getWidth();
        if (minWidth != null && minWidth > 0) {
            width = minWidth;
        }
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
                .setSearchInputHint(searchInputHint)
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
        if (itemTextMapper != null) {
            text = itemTextMapper.getText(t);
        } else if (t instanceof String) {
            text = (String) t;
        } else if (t instanceof DataTextImpl) {
            text = ((DataTextImpl) t).getText();
        } else {
            text = t.toString();
        }
        return text;
    }
}
