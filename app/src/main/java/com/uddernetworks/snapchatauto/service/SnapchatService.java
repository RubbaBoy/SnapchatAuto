package com.uddernetworks.snapchatauto.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SnapchatService {
    List<UserData> getUsers();

    default CompletableFuture<List<UserData>> getUsersAsync() {
        return CompletableFuture.supplyAsync(this::getUsers);
    }

    void refresh();

    default CompletableFuture<Void> refreshAsync() {
        return CompletableFuture.runAsync(this::refresh);
    }
}
