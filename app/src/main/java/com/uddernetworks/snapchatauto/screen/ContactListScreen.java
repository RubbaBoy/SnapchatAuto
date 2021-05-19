package com.uddernetworks.snapchatauto.screen;

import static androidx.car.app.model.CarColor.BLUE;
import static androidx.car.app.model.CarColor.RED;

import static com.uddernetworks.snapchatauto.Executors.UI_EXECUTOR;

import android.os.Handler;
import android.text.SpannableString;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.ScreenManager;
import androidx.car.app.model.Action;
import androidx.car.app.model.ItemList;
import androidx.car.app.model.ListTemplate;
import androidx.car.app.model.MessageTemplate;
import androidx.car.app.model.Pane;
import androidx.car.app.model.PaneTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.Futures;
import com.uddernetworks.snapchatauto.Utility;
import com.uddernetworks.snapchatauto.service.SnapAccessibilityService;
import com.uddernetworks.snapchatauto.service.SnapEventHandler;
import com.uddernetworks.snapchatauto.service.UserData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ContactListScreen extends Screen implements DefaultLifecycleObserver {

    private final Handler mHandler = new Handler();
    private List<UserData> users = new ArrayList<>();

    public ContactListScreen(@NonNull CarContext carContext) {
        super(carContext);
        getLifecycle().addObserver(this);
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
//        System.out.println(".onGetTemplate");
//        var paneBuilder = new Pane.Builder();

        var listBuilder = new ItemList.Builder();
//        var rowBuilder = new Row.Builder();

//        var service = SnapAccessibilityService.getInstance();

        /* new ListTemplate.Builder()
                .setSingleList(listBuilder.build())
                .setTitle("Chat")
                .setHeaderAction(Action.APP_ICON)
                .build() */

        users.stream().map(this::createRow).forEach(listBuilder::addItem);

        return new ListTemplate.Builder()
                .setSingleList(listBuilder.build())
                .setTitle("Chat")
                .setHeaderAction(Action.BACK)
                .build();

//        var pane = new Pane.Builder();

//        return new PaneTemplate.Builder(pane.build())
//                .setTitle("Chat")
//                .build();
    }

    public void update() {
        try {
//            var screen = getCarContext().getCarService(ScreenManager.class);
//            screen.pop();

            System.out.println("Update");

            var service = SnapAccessibilityService.getInstance();
            Futures.transformAsync(
                    Futures.submitAsync(
                            () ->
                                    Futures.immediateFuture(service.getUsers()),
                            UI_EXECUTOR),
                    users -> {
                        this.users = users;
                        invalidate();
                        return null;
                    },
                    UI_EXECUTOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Row createRow(UserData data) {
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
//                    .setOnClickListener(() -> {
//                        // getScreenManager()
//                        //        .push(
//                        //                new SelectableListsDemoScreen(
//                        //                        getCarContext()))
//                        System.out.println("Clicked " + data.getName());
//                    })
                .build();
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
//                System.out.println("Update");
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
