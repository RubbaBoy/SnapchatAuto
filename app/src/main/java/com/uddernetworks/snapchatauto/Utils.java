package com.uddernetworks.snapchatauto;

import android.text.Spannable;
import android.text.SpannableString;

import androidx.car.app.model.CarColor;
import androidx.car.app.model.ForegroundCarColorSpan;

public abstract class Utils {

    public static SpannableString colorize(String s, CarColor color, int index,
                                        int length) {
        SpannableString ss = new SpannableString(s);
        ss.setSpan(
                ForegroundCarColorSpan.create(color),
                index,
                index + length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public static SpannableString colorize(String string, CarColor color) {
        return colorize(string, color, 0, string.length());
    }

    public static SpannableString colorize(String string, String trailing, CarColor color) {
        return colorize(string + trailing, color, 0, string.length());
    }

    public static SpannableString colorize(String leading, String string, String trailing, CarColor color) {
        return colorize(leading + string + trailing, color, leading.length(), string.length());
    }

    private Utils() {
    }
}