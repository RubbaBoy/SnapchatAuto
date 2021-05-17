package com.uddernetworks.snapchatauto.screen;

import android.content.pm.ApplicationInfo;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.car.app.CarAppService;
import androidx.car.app.Session;
import androidx.car.app.validation.HostValidator;

public class MainService extends CarAppService {
    public static final String SHARED_PREF_KEY = "ShowcasePrefs";
    public static final String PRE_SEED_KEY = "PreSeed";

//    /** Creates a deep link URI with the given deep link action. */
//    @NonNull
//    public static Uri createDeepLinkUri(@NonNull String deepLinkAction) {
//        return Uri.fromParts(ShowcaseSession.URI_SCHEME, ShowcaseSession.URI_HOST, deepLinkAction);
//    }

    @Override
    @NonNull
    public Session onCreateSession() {
        return new MainSession();
    }

    @NonNull
    @Override
    public HostValidator createHostValidator() {
        if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR;
        } else {
            return new HostValidator.Builder(getApplicationContext())
                    .addAllowedHosts(androidx.car.app.R.array.hosts_allowlist_sample)
                    .build();
        }
    }
}
