package com.uddernetworks.snapchatauto.screen;

import static androidx.car.app.model.CarColor.BLUE;
import static androidx.car.app.model.CarColor.RED;

import android.text.SpannableString;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.model.Action;
import androidx.car.app.model.ItemList;
import androidx.car.app.model.ListTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.uddernetworks.snapchatauto.Utility;
import com.uddernetworks.snapchatauto.service.SnapAccessibilityService;
import com.uddernetworks.snapchatauto.service.SnapEventHandler;
import com.uddernetworks.snapchatauto.service.UserData;

import java.util.List;
import java.util.stream.Collectors;

public class ContactListScreen extends Screen implements DefaultLifecycleObserver {

    public ContactListScreen(@NonNull CarContext carContext) {
        super(carContext);
        getLifecycle().addObserver(this);
    }

    @NonNull
    @Override
    public Template onGetTemplate() {

        var listBuilder = new ItemList.Builder();

        try {
            var service = SnapAccessibilityService.getInstance();

            service.getUsers().forEach(user -> {
                var created = createRow(user);
                if (created == null) {
                    System.out.println("Created null");
                }
                listBuilder.addItem(created);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ListTemplate.Builder()
                .setSingleList(listBuilder.build())
                .setTitle("Chat")
                .setHeaderAction(Action.APP_ICON)
                .build();
    }

    public void update() {
        try {
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Row createRow(UserData data) {
        try {
            SpannableString prefix;

            var info = data.getChatInfo();

            var streak = data.getStreak() == null ? "" : " · " + data.getStreak();
            var emojis = data.getEmojis() == null ? "" : " · " + data.getEmojis();
            var time = data.getTime() == null ? "" : " · " + data.getTime();
            var trailing = time + streak + emojis;

            if (data.hasNewChat()) {
                prefix = Utility.color("New Chat", trailing, BLUE);
            } else if (data.hasNewSnap()) {
                prefix = Utility.color("New Snap", trailing, RED);
            } else if (data.isTyping()) {
                prefix = Utility.color("Typing...", trailing, BLUE);
            } else {
                prefix = new SpannableString(info + trailing);
            }

            if (data.getName() == null) {
                System.out.println("NAME IS NULL!");
            }

            return new Row.Builder()
                    .setTitle(data.getName())
                    .addText(prefix)
                    .setOnClickListener(() -> {
                        // getScreenManager()
                        //        .push(
                        //                new SelectableListsDemoScreen(
                        //                        getCarContext()))
                        System.out.println("Clicked " + data.getName());
                    })
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private SnapEventHandler snapHandler;

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        var service = SnapAccessibilityService.getInstance();
        service.addEventHandler(snapHandler = new SnapEventHandler() {
            @Override
            public void onTypingUpdate(List<UserData> typingUsers) {
                System.out.println("Typing: " + typingUsers.stream().map(UserData::getName).collect(Collectors.joining(", ")));
            }

            @Override
            public void update() {
                System.out.println("Update");
                ContactListScreen.this.update();
            }
        });
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        var service = SnapAccessibilityService.getInstance();
        service.removeEventHandler(snapHandler);
    }
}
