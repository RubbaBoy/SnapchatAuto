package com.uddernetworks.snapchatauto.service;

import android.view.accessibility.AccessibilityNodeInfo;

public class UserData {
    private final String name;
    private final AccessibilityNodeInfo userInfo;
    private final String chatInfo;
    private final String time;
    private final String streak;

    public UserData(String name, AccessibilityNodeInfo userInfo, String chatInfo, String time, String streak) {
        this.name = name;
        this.userInfo = userInfo;
        this.chatInfo = chatInfo;
        this.time = time;
        this.streak = streak;
    }

    public String getName() {
        return name;
    }

    public AccessibilityNodeInfo getUserInfo() {
        return userInfo;
    }

    public String getChatInfo() {
        return chatInfo;
    }

    public String getTime() {
        return time;
    }

    public String getStreak() {
        return streak;
    }

    public boolean hasNewChat() {
        return chatInfo.startsWith("New Chat");
    }

    @Override
    public String toString() {
        return "UserData{" +
                "name='" + name + '\'' +
                ", chatInfo='" + chatInfo + '\'' +
                ", time='" + time + '\'' +
                ", streak='" + streak + '\'' +
                '}';
    }
}
