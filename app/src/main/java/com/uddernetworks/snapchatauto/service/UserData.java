package com.uddernetworks.snapchatauto.service;

import android.view.accessibility.AccessibilityNodeInfo;

public class UserData {
    private final String name;
    private final AccessibilityNodeInfo userInfo;
    private final String chatInfo;
    private final String time;
    private final String streak;
    private final String emojis;

    public UserData(String name, AccessibilityNodeInfo userInfo, String chatInfo, String time, String streak, String emojis) {
        this.name = name;
        this.userInfo = userInfo;
        this.chatInfo = chatInfo;
        this.time = time;
        this.streak = streak;
        this.emojis = emojis;
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

    public String getEmojis() {
        return emojis;
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
