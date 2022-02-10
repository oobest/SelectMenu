package com.example.myapplication.menu;

import android.text.TextUtils;

import androidx.core.text.HtmlCompat;

/**
 * @author albert
 * @date 2022/1/21 9:46
 * @mail 416392758@@gmail.com
 * @since v1
 */
public class TextViewMarkUtils {

    private static final String TEXT_FORMAT = "<strong><font color= '#3a598f'>%s</font></strong>";

    public static CharSequence format(String string, String s) {
        if (TextUtils.isEmpty(s) || !string.contains(s)) {
            return string;
        } else {
            String replaceContent = String.format(TEXT_FORMAT, s);
            String newText = string.replace(s, replaceContent);
            return HtmlCompat.fromHtml(newText, HtmlCompat.FROM_HTML_MODE_COMPACT);
        }
    }

}
