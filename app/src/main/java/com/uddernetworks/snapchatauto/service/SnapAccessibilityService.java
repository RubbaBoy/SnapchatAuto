package com.uddernetworks.snapchatauto.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SnapAccessibilityService extends AccessibilityService {

    private static SnapAccessibilityService instance;
    private boolean first = true;

    @Override
    protected void onServiceConnected() {
        var info = getServiceInfo();

        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.snapchat.android"};

        super.setServiceInfo(info);

        instance = this;

        first = true;

        init();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!event.getClassName().equals("com.snap.mushroom.MainActivity")) { // Text is Snapchat
            return;
        }

        if (first && event.getEventType() == AccessibilityEvent.WINDOWS_CHANGE_ACTIVE) {
            first = false;

            var root = getRootInActiveWindow(); // FrameLayout

            var bottomRow = root.getChild(root.getChildCount() - 1);

            var location = bottomRow.getChild(0);
            var chat = bottomRow.getChild(1);
            var camera = bottomRow.getChild(2);
            var stories = bottomRow.getChild(3);
            var spotlight = bottomRow.getChild(4);

            chat.performAction(AccessibilityNodeInfo.ACTION_CLICK);

            root = getRootInActiveWindow();
            var chatText = root.findAccessibilityNodeInfosByText("Chat");
            if (chatText.isEmpty()) {
                return;
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {}

            printChild(getRootInActiveWindow(), 0);

            var users = getUsers();

            System.out.println("users = " + users);

            System.out.println("New chat: " + users.stream().filter(data -> data.getChatInfo().startsWith("New Chat")).map(UserData::getName).collect(Collectors.joining(", ")));
        }
    }

    public List<UserData> getUsers() {
        var root = getRootInActiveWindow();

        var users = new ArrayList<UserData>();

        var userList = root.getChild(0);
        for (int i = 0; i < userList.getChildCount(); i++) {
            var user = userList.getChild(i).getChild(0); // FrameLayout > View

            var all = getChildren(user);
            var chatInfo = all.get(all.size() - 1); // New Chat, Delivered, etc.
            var name = all.get(all.size() - 3); // The name

            var text = chatInfo.getText().toString().split(" {5}");
            String streak = null;
            if (text.length >= 3) {
                streak = text[2];
            }

            users.add(new UserData(name.getText().toString(), user, text[0], text[1], streak));
        }

        return users;
    }

    private List<AccessibilityNodeInfo> getChildren(AccessibilityNodeInfo node) {
        var list = new ArrayList<AccessibilityNodeInfo>();
        for (int i = 0; i < node.getChildCount(); i++) {
            list.add(node.getChild(i));
        }
        return list;
    }

    @Override
    public void onInterrupt() {

    }

    private void waitFor(String text) {
        while (getRootInActiveWindow().findAccessibilityNodeInfosByText(text).isEmpty()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {}
        }
    }

    public static void ready() {
        if (instance != null) {
            instance.init();
        }
    }

    public void init() {
        var launchIntent = getPackageManager().getLaunchIntentForPackage("com.snapchat.android");
        if (launchIntent != null) {
            startActivity(launchIntent);
        }
    }

    private void printChild(AccessibilityNodeInfo info, int depth) {
        for (int i = 0; i < depth * 2; i++) {
            System.out.print(" ");
        }

        var bounds = new Rect();
        info.getBoundsInScreen(bounds);
        System.out.print(info.getClassName() + " " + bounds + " " + info.getViewIdResourceName());

        if (info.getText() != null) {
            System.out.print(" " + info.getText());
        }

        System.out.println();

        for (int i = 0; i < info.getChildCount(); i++) {
            printChild(info.getChild(i), depth + 1);
        }
    }

    public static SnapAccessibilityService getInstance() {
        return instance;
    }
}
