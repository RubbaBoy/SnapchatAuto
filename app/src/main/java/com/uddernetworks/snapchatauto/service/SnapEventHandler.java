package com.uddernetworks.snapchatauto.service;

import java.util.List;

public interface SnapEventHandler {

    void onTypingUpdate(List<UserData> typingUsers);

    /**
     * A general update event, meaning the Android Auto app should update its view.
     */
    void update();

}
