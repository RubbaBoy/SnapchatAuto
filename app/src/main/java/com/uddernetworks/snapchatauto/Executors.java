package com.uddernetworks.snapchatauto;

import java.util.concurrent.ExecutorService;
//import com.google.common.util.concurrent.ListeningExecutorService;

public abstract class Executors {

    /** An executor that runs its tasks in the UI thread. */
    public static final ExecutorService UI_EXECUTOR = UiExecutor.get();

    private Executors() {}
}
