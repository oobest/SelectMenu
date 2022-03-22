package com.albert.selectmenu;

/**
 * @author albert
 * @date 2022/1/20 16:30
 * @mail 416392758@@gmail.com
 * @since v1
 */
public interface SameItemImpl<Data> {
    boolean isSame(Data data1, Data data2);
}