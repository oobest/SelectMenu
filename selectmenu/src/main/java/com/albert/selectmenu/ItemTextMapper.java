package com.agrivo.selectmenu;

/**
 * 显示内容映射方法
 * @author albert
 * @date 2022/2/10 16:45
 * @mail 416392758@@gmail.com
 * @since v1
 */
public interface ItemTextMapper<Data> {
    String getText(Data data);
}
