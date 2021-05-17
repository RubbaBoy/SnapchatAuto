package com.uddernetworks.snapchatauto.screen;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.ScreenManager;
import androidx.car.app.Session;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

public class MainSession extends Session implements DefaultLifecycleObserver {
    @NonNull
    @Override
    public Screen onCreateScreen(@NonNull Intent intent) {
        System.out.println(".onCreateScreen");
        Lifecycle lifecycle = getLifecycle();
        lifecycle.addObserver(this);

        if (CarContext.ACTION_NAVIGATE.equals(intent.getAction())) {
            // Handle the navigation Intent by pushing first the "home" screen onto the stack, then
            // returning the screen that we want to show a template for.
            // Doing this allows the app to go back to the previous screen when the user clicks on a
            // back
            // action.
            getCarContext()
                    .getCarService(ScreenManager.class)
                    .push(new ContactListScreen(getCarContext()));
            return new ContactListScreen(getCarContext());
        }

        // For demo purposes this uses a shared preference setting to store whether we should
        // pre-seed
        // the screen back stack.  This allows the app to have a way to go back to the home/start
        // screen
        // making the home/start screen the 0th position.
        // For a real application, it would probably check if it has all the needed system
        // permissions,
        // and if any are missing, it would pre-seed the start screen and return a screen that will
        // send
        // the user to the phone to grant the needed permissions.
//        boolean shouldPreSeedBackStack =
//                getCarContext()
//                        .getSharedPreferences(ShowcaseService.SHARED_PREF_KEY, Context.MODE_PRIVATE)
//                        .getBoolean(ShowcaseService.PRE_SEED_KEY, false);
//        if (shouldPreSeedBackStack) {
//            // Reset so that we don't require it next time
//            getCarContext()
//                    .getSharedPreferences(ShowcaseService.SHARED_PREF_KEY, Context.MODE_PRIVATE)
//                    .edit()
//                    .putBoolean(ShowcaseService.PRE_SEED_KEY, false)
//                    .apply();
//
//            getCarContext()
//                    .getCarService(ScreenManager.class)
//                    .push(new ContactListScreen(getCarContext()));
//            return new PreSeedingFlowScreen(getCarContext());
//        }
        return new ContactListScreen(getCarContext());
    }

//    @Override
//    public void onStart(@NonNull LifecycleOwner owner) {
//        getCarContext()
//                .registerReceiver(
//                        mReceiver, new IntentFilter(GoToPhoneScreen.PHONE_COMPLETE_ACTION));
//    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
//        getCarContext().unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        Log.i("SHOWCASE", "onDestroy");
    }

    @Override
    public void onNewIntent(@NonNull Intent intent) {
        // Process various deeplink intents.

        ScreenManager screenManager = getCarContext().getCarService(ScreenManager.class);

        if (CarContext.ACTION_NAVIGATE.equals(intent.getAction())) {
            // If the Intent is to navigate, and we aren't already, push the navigation screen.
            if (screenManager.getTop() instanceof ContactListScreen) {
                return;
            }
            screenManager.push(new ContactListScreen(getCarContext()));
            return;
        }

//        Uri uri = intent.getData();
//        if (uri != null
//                && URI_SCHEME.equals(uri.getScheme())
//                && URI_HOST.equals(uri.getSchemeSpecificPart())) {
//
//            Screen top = screenManager.getTop();
//            switch (uri.getFragment()) {
//                case DeepLinkNotificationReceiver.INTENT_ACTION_PHONE:
//                    getGoToPhoneScreenAndSetItAsTop();
//                    break;
//                case DeepLinkNotificationReceiver.INTENT_ACTION_CANCEL_RESERVATION:
//                    if (!(top instanceof ReservationCancelledScreen)) {
//                        screenManager.push(new ReservationCancelledScreen(getCarContext()));
//                    }
//                    break;
//                case DeepLinkNotificationReceiver.INTENT_ACTION_NAV_NOTIFICATION_OPEN_APP:
//                    if (!(top instanceof NavigationNotificationsDemoScreen)) {
//                        screenManager.push(new NavigationNotificationsDemoScreen(getCarContext()));
//                    }
//                    break;
//                default:
//                    // No-op
//            }
//        }
    }
}
