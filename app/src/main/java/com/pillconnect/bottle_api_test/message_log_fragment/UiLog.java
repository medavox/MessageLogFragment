package com.pillconnect.bottle_api_test.message_log_fragment;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Adam Howard
 * Use this class to post a message on the MessageLogFragment.
 * Call {@link UiLog#post(String)} from anywhere, on any thread.
 * Initialise before first use with {@link UiLog#init(Context)}.
 *         Created on 24/01/18.
 */
public final class UiLog {
    public static final Queue<LogMessage> pendingWrites = new ConcurrentLinkedQueue<LogMessage>();
    //private static boolean pendingWriteQueueConsumerIsRunning = false;
    //private static ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final String TAG = "UiLog";
    private static WeakReference<Context> context;

    private UiLog() throws IllegalAccessException {
        throw new IllegalAccessException("this class is not meant to be instantiated.");
    }
    /**Initialise this class with a Context object.
     * This method must be called at least once before {@link #post(String)} can be called successfully.
     * NOTE: if the supplied {@link Context} object ever becomes invalid,
     * then this class will need to be initialised again.
     * @param ctx A valid Context object for your app.*/
    public static void init(Context ctx) {
        if(isInitialised()) {
            Log.w(TAG, "Re-initialising! Replacing old context \""+context.get()+"\" with new Context:"+ctx);
        }
        context = new WeakReference<Context>(ctx);
    }

    /**Whether this class is currently initialised with a valid {@link Context} object.
     * @return {@code context == null || context.get() == null},
     * where {@code context} is a {@code WeakReference<Context>}.*/
    public static boolean isInitialised() {
        return context == null || context.get() == null;
    }

    /**Call this method to post a message to the MessageLogFragment Ui component.
     * @param message the message to post. Must not be null or empty.*/
    public static void post(@NonNull String message) {
        if(!isInitialised()) {
            new IllegalStateException("Not initialised! Please call Initialise(Context) " +
                    "at least once before calling post(String).").printStackTrace();
            return;
        }
        if(TextUtils.isEmpty(message)) {
            new Exception("null or empty value passed to MessageLogFragment!").printStackTrace();
            return;
        }
        LogMessage logMessage = new LogMessage(message);
        pendingWrites.add(logMessage);
        LocalBroadcastManager.getInstance(context.get())
                .sendBroadcast(new Intent(MessageLogFragment.INTENT_ACTION));
    }
}
