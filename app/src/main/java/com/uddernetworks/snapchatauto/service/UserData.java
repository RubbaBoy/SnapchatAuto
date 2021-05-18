package com.uddernetworks.snapchatauto.service;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Objects;

public class UserData {
    private final String name;
    private final AccessibilityNodeInfo userInfo;
    private final String chatInfo;
    private final String time;
    private final String streak;
    private final String emojis;
    private final boolean typing;

    public UserData(String name, AccessibilityNodeInfo userInfo, String chatInfo, String time, String streak, String emojis, boolean typing) {
        this.name = name;
        this.userInfo = userInfo;
        this.chatInfo = chatInfo;
        this.time = time;
        this.streak = streak;
        this.emojis = emojis;
        this.typing = typing;
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

    public boolean isTyping() {
        return typing;
    }

    public boolean hasNewChat() {
        return chatInfo != null && chatInfo.startsWith("New Chat");
    }

    public boolean hasNewSnap() {
        return chatInfo != null && chatInfo.startsWith("New Snap");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserData userData = (UserData) o;
        return typing == userData.typing && Objects.equals(name, userData.name) && Objects.equals(chatInfo, userData.chatInfo) && Objects.equals(time, userData.time) && Objects.equals(streak, userData.streak) && Objects.equals(emojis, userData.emojis);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, chatInfo, time, streak, emojis, typing);
    }
}
