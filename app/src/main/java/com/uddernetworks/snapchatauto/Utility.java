package com.uddernetworks.snapchatauto;

import android.text.Spannable;
import android.text.SpannableString;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.car.app.model.CarColor;
import androidx.car.app.model.ForegroundCarColorSpan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Utility {

    public static SpannableString color(String s, CarColor color, int index,
                                        int length) {
        SpannableString ss = new SpannableString(s);
        ss.setSpan(
                ForegroundCarColorSpan.create(color),
                index,
                index + length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public static SpannableString color(String string, CarColor color) {
        return color(string, color, 0, string.length());
    }

    public static SpannableString color(String string, String trailing, CarColor color) {
        return color(string + trailing, color, 0, string.length());
    }

    public static SpannableString color(String leading, String string, String trailing, CarColor color) {
        return color(leading + string + trailing, color, leading.length(), string.length());
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {}
    }

    /**
     * Checks if the given info matches the given list, top to lowest level.
     * For example, if you were matching a ViewGroup (as info) with a parent of FrameLayour, the
     * parents would be
     * <code>android.view.ViewGroup, android.widget.FrameLayout</code>
     * @param info The node
     * @param parents The parents' class paths, from highest to lowest level
     * @return If it matches
     */
    public static boolean matchesTree(AccessibilityNodeInfo info, String... parents) {
        return matchesTree(info, Arrays.asList(parents));
    }

    /**
     * Checks if the given info matches the given list, top to lowest level.
     * For example, if you were matching a ViewGroup (as info) with a parent of FrameLayour, the
     * parents would be
     * <code>android.view.ViewGroup, android.widget.FrameLayout</code>
     * @param info The node
     * @param parents The parents' class paths, from highest to lowest level
     * @return If it matches
     */
    public static boolean matchesTree(AccessibilityNodeInfo info, List<String> parents) {
        if (info == null) {
            return parents.isEmpty();
        }

        if (!info.getClassName().equals(parents.get(0))) {
            return false;
        }

        return matchesTree(info.getParent(), parents.subList(1, parents.size()));
    }

    public static List<AccessibilityNodeInfo> getFromPath(AccessibilityNodeInfo root, String... path) {
        return getFromPath(root, Arrays.asList(path));
    }

    public static List<AccessibilityNodeInfo> getFromPath(AccessibilityNodeInfo root, List<String> path) {
        if (path.isEmpty()) {
            return Collections.singletonList(root);
        }

        var head = path.get(0);
        var subbed = path.subList(1, path.size());
        return getChildren(root)
                .stream()
                .filter(node -> node.getClassName().equals(head))
                .flatMap(node -> getFromPath(node, subbed).stream())
                .collect(Collectors.toList());
    }

    public static List<AccessibilityNodeInfo> getChildren(AccessibilityNodeInfo node) {
        var list = new ArrayList<AccessibilityNodeInfo>();
        for (int i = 0; i < node.getChildCount(); i++) {
            var child = node.getChild(i);
            if (child != null) {
                list.add(child);
            }
        }
        return list;
    }
}