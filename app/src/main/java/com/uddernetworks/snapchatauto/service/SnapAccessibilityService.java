package com.uddernetworks.snapchatauto.service;

import static com.uddernetworks.snapchatauto.Utility.sleep;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.uddernetworks.snapchatauto.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SnapAccessibilityService extends AccessibilityService implements SnapchatService {

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
//        System.out.println(Integer.toHexString(event.getEventType()) + " > " + event.getClassName() + " " + event.getSource());

        if (first) {
            if (!event.getClassName().equals("com.snap.mushroom.MainActivity")) { // Text is Snapchat
                return;
            }

            if (event.getEventType() == AccessibilityEvent.WINDOWS_CHANGE_ACTIVE) {
                first = false;

                var root = getRootInActiveWindow(); // FrameLayout

                click(NavButton.CHAT.getButton(this));

                root = getRootInActiveWindow();
                var chatText = root.findAccessibilityNodeInfosByText("Chat");
                if (chatText.isEmpty()) {
                    return;
                }

                var users = getUsers();

                printChild(getRootInActiveWindow(), 0);

                System.out.println("users = " + users);

                System.out.println("New chat: " + users.stream().filter(data -> data.getChatInfo().startsWith("New Chat")).map(UserData::getName).collect(Collectors.joining(", ")));
            }
        } else {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                var source = event.getSource();

                if (source == null) {
                    System.out.println("Source null");
                    return;
                }

                do {
                    System.out.print(source.getClassName() + " ");
                } while ((source = source.getParent()) != null);
                System.out.println();
            }
        }

        // androidx.recyclerview.widget.RecyclerView getClassName() for typing
    }

    @Override
    public List<UserData> getUsers() {
        var root = getRootInActiveWindow();

        var users = new ArrayList<UserData>();

        for (var userNode : Utility.getFromPath(root, "androidx.recyclerview.widget.RecyclerView", "android.widget.FrameLayout", "android.view.View")) { // Views
            var userTexts = Utility.getFromPath(userNode, "javaClass")
                    .stream()
                    .map(AccessibilityNodeInfo::getText)
                    .map(CharSequence::toString)
                    .toArray(String[]::new);

            String emojis = null, streak = null;
            var offset = 0;
            if (userTexts.length == 3) {
                emojis = userTexts[0];
                offset = 1;
            }

            var text = userTexts[offset + 1].split(" {5}");
            if (text.length == 1 && text[0].equals("Double tap to reply")) {
                sleep(100);
                continue;
            }

            if (text.length >= 3) {
                streak = text[2];
            }

            if (text.length >= 2) {
                users.add(new UserData(userTexts[offset], userNode, text[0], text[1], streak, emojis));
            }
        }

        return users;
    }

    @Override
    public void refresh() {
        click(NavButton.CAMERA.getButton(this));
        click(NavButton.CHAT.getButton(this));
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * Waits for a node with the given text to exist
     * @param text The text
     */
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

    enum NavButton {
        LOCATION,
        CHAT,
        CAMERA,
        STORIES,
        SPOTLIGHT;

        public AccessibilityNodeInfo getButton(AccessibilityService service) {
            var root = service.getRootInActiveWindow();
            if (root == null) {
                System.out.println("UH OH ROOT NULL!!!!");
            }

            var bottomRow = root.getChild(root.getChildCount() - 1);
            return bottomRow.getChild(ordinal());
        }
    }

    private static void click(AccessibilityNodeInfo node) {
        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }
}
