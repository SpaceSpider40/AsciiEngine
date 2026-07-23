package com.engine;

import com.engine.graphics.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

/*
 * Display debug messages on screen. Engine errors or user defined messages.
 */
public class Debug {
    public record Message(
            String msg,
            Color color
    ) {
    }

    public static final long DISPLAY_TIME = 5000000000L;

    private static final ConcurrentSkipListMap<Long, Message> messages      = new ConcurrentSkipListMap<>();
    private static final AtomicLong                           lastTimestamp = new AtomicLong();

    public static void log(String message) {
        if (message.isBlank()) return;

        messages.put(getUniqueTimestamp(), new Message(message, Color.WHITE));
    }

    public static void warn(String message) {
        if (message.isBlank()) return;

        messages.put(getUniqueTimestamp(), new Message(message, Color.YELLOW));
    }

    public static void err(String message) {
        if (message.isBlank()) return;

        messages.put(getUniqueTimestamp(), new Message(message, Color.RED));
    }

    public static Collection<Message> getMessages() {
        ArrayList<Long> keysToRemove = new ArrayList<>();
        for (Long messageTime : messages.keySet()) {
            if (System.nanoTime() - messageTime <= DISPLAY_TIME) {
                continue;
            }

            //message is old remove it from display
            keysToRemove.add(messageTime);
        }

        keysToRemove.forEach(messages::remove);

        return messages.values();
    }

    private static long getUniqueTimestamp() {
        long now = System.nanoTime();
        while (true) {
            long last = lastTimestamp.get();
            long next = Math.max(now, last + 1);
            if (lastTimestamp.compareAndSet(last, next)){
                return next;
            }
        }
    }
}
