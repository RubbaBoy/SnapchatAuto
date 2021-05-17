package com.uddernetworks.snapchatauto.screen;

import static androidx.car.app.model.CarColor.BLUE;
import static androidx.car.app.model.CarColor.RED;

import android.text.SpannableString;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.model.Action;
import androidx.car.app.model.CarText;
import androidx.car.app.model.ItemList;
import androidx.car.app.model.ListTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;

import com.uddernetworks.snapchatauto.Utils;
import com.uddernetworks.snapchatauto.service.SnapAccessibilityService;
import com.uddernetworks.snapchatauto.service.UserData;

public class ContactListScreen extends Screen {

    public ContactListScreen(@NonNull CarContext carContext) {
        super(carContext);
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        var service = SnapAccessibilityService.getInstance();

        var listBuilder = new ItemList.Builder();

        service.getUsers().forEach(user -> listBuilder.addItem(createRow(user)));
//        listBuilder.addItem(createRow(new UserData("Billy Jhones", null, "New Snap", "12m", "74\uD83D\uDD25")));

        return new ListTemplate.Builder()
                .setSingleList(listBuilder.build())
                .setTitle("Chat")
                .setHeaderAction(Action.APP_ICON)
                .build();
    }

    private Row createRow(UserData data) {
        SpannableString prefix;

        var info = data.getChatInfo();

        var streak = data.getStreak() == null ? "" : " · " + data.getStreak();
        var trailing = " · " + data.getTime() + streak;

        if (info.startsWith("New Chat")) {
            prefix = Utils.colorize("New Chat", trailing, BLUE);
        } else if (info.startsWith("New Snap")) {
            prefix = Utils.colorize("New Snap", trailing, RED);
        } else {
            prefix = new SpannableString(info + trailing);
        }

        return new Row.Builder()
                .setTitle(data.getName())
                .addText(prefix) //  + " · " + data.getTime() + streak
                .setOnClickListener(() -> {
                    // getScreenManager()
                    //        .push(
                    //                new SelectableListsDemoScreen(
                    //                        getCarContext()))
                    System.out.println("Clicked " + data.getName());
                })
                .build();
    }
}
