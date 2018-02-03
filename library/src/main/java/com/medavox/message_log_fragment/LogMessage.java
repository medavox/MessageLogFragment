package com.medavox.message_log_fragment;

/**
 * @author Adam Howard
 * @date 23/01/18
 */
final class LogMessage {
    public final String message;
    public final long time;

    public LogMessage(String msg) {
        this.message = msg;
        this.time = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "message: \"" + message + "\" at " + time;
    }
}
