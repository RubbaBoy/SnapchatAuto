package com.uddernetworks.snapchatauto.screen;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.model.Action;
import androidx.car.app.model.CarColor;
import androidx.car.app.model.MessageTemplate;
import androidx.car.app.model.Template;

public class HomeScreen extends Screen {

    public HomeScreen(@NonNull CarContext carContext) {
        super(carContext);
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        return new MessageTemplate.Builder("What you wanna do")
                .setTitle("Snapchat Auto")
                .addAction(new Action.Builder()
                        .setBackgroundColor(CarColor.BLUE)
                        .setOnClickListener(() -> getScreenManager()
                                .push(
                                        new ContactListScreen(
                                                getCarContext())))
                        .setTitle("Go to stuff")
                        .build())
                .build();
    }
}
