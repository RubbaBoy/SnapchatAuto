package com.uddernetworks.snapchatauto.service;

import static com.uddernetworks.snapchatauto.Utility.getChildren;
import static com.uddernetworks.snapchatauto.Utility.sleep;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.uddernetworks.snapchatauto.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SnapAccessibilityService extends AccessibilityService implements SnapchatService {

    private static SnapAccessibilityService instance;
    private boolean first = true;
    private final List<SnapEventHandler> handlers = new ArrayList<>();

    @Override
    protected void onServiceConnected() {
        var info = getServiceInfo();

        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.snapchat.android"};

        super.setServiceInfo(info);

        instance = this;

        first = true;

//        var snapHandler = new SnapEventHandler() {
//            @Override
//            public void onTypingUpdate(List<UserData> typingUsers) {
//                System.out.println("Typing: " + typingUsers.stream().map(UserData::getName).collect(Collectors.joining(", ")));
//            }
//
//            @Override
//            public void update() {
//                System.out.println("Update!");
//            }
//        };
//        addEventHandler(snapHandler);

        init();
    }

    private List<UserData> lastTyping;
    private List<UserData> allUsers;

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

                NavButton.CHAT.getButton(this).ifPresent(SnapAccessibilityService::click);

                sleep(500);

                root = getRootInActiveWindow();
                var chatText = root.findAccessibilityNodeInfosByText("Chat");
                if (chatText.isEmpty()) {
                    System.out.println("Chat empty");
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

                // Might be a typing or update
                if (source.getClassName().equals("androidx.recyclerview.widget.RecyclerView")) {
//                    var userViews = Utility.getFromPath(getRootInActiveWindow(), "androidx.recyclerview.widget.RecyclerView", "android.widget.FrameLayout", "android.view.View")
//                            .stream()
//                            .map(this::userDataFromView)
//                            .filter(Optional::isPresent)
//                            .map(Optional::get)
//                            .collect(Collectors.toList());

                    sleep(500);

                    var userViews = getUsers();

                    var typingUsers = userViews
                            .stream()
                            .filter(UserData::isTyping)
                            .collect(Collectors.toList());

                    if (!typingUsers.equals(lastTyping)) {
                        handlers.forEach(handler -> handler.onTypingUpdate(typingUsers));
                        lastTyping = typingUsers;
                    }

                    if (!userViews.equals(allUsers)) {
                        allUsers = userViews;
                        handlers.forEach(SnapEventHandler::update);
                    }
                }
            }
        }
    }

    @Override
    public void addEventHandler(SnapEventHandler handler) {
        handlers.add(handler);
    }

    @Override
    public void removeEventHandler(SnapEventHandler handler) {
        handlers.remove(handler);
    }

    @Override
    public List<UserData> getUsers() {
        var root = getRootInActiveWindow();

        var users = new ArrayList<UserData>();

        for (var userNode : Utility.getFromPath(root, "androidx.recyclerview.widget.RecyclerView", "android.widget.FrameLayout", "android.view.View")) { // Views
            var userOptional = userDataFromView(userNode);
            if (!userOptional.isPresent()) {
                System.out.println("Not present!");
//                sleep(100);
//                return getUsers();
                continue;
            }

            users.add(userOptional.get());
        }

        return users;
    }

    /**
     * Creates a {@link UserData} from a android.view.View
     * @param view The view of class type android.view.View
     * @return The {@link UserData}. If empty, it should be retried
     */
    private Optional<UserData> userDataFromView(AccessibilityNodeInfo view) {
        var userTexts = Utility.getFromPath(view, "javaClass")
                .stream()
                .map(AccessibilityNodeInfo::getText)
                .map(CharSequence::toString)
                .toArray(String[]::new);

        String emojis = null, streak = null, chatInfo = null, time = null;
        var typing = false;
        var offset = 0;
        if (userTexts.length == 3) {
            emojis = userTexts[0];
            offset = 1;
        }

        var text = userTexts[offset + 1].split(" {5}");
//        System.out.println("text = " + Arrays.toString(text));

        if (text.length == 1) {
            if (text[0].equals("Double tap to reply")) {
                return Optional.empty();
            } else if (text[0].equals("Typing...")) {
                typing = true;
            }
        } else {
            chatInfo = text[0];
            time = text[1];
        }

        if (text.length >= 3) {
            streak = text[2];
        }

        return Optional.of(new UserData(userTexts[offset], view, chatInfo, time, streak, emojis, typing));
    }

    @Override
    public void refresh() {
        var camOp = NavButton.CAMERA.getButton(this);
        var chatOp = NavButton.CHAT.getButton(this);
        if (!camOp.isPresent() || !chatOp.isPresent()) {
            System.out.println("Not present!!!!!!!!!!!!!!!!!!!!");
            sleep(100);
            refresh();
        } else {
            click(camOp.get());
            click(chatOp.get());
        }
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

        public Optional<AccessibilityNodeInfo> getButton(AccessibilityService service) {
            var root = service.getRootInActiveWindow();
            if (root == null) {
                System.out.println("UH OH ROOT NULL!!!!");
                return Optional.empty();
            }
            var bottomRow = root.getChild(root.getChildCount() - 1);
            return Optional.ofNullable(bottomRow.getChild(ordinal()));
        }
    }

    private static void click(AccessibilityNodeInfo node) {
        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }
}
