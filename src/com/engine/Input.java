package com.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Input {

    private static final Set<Integer> keysDown = ConcurrentHashMap.newKeySet();
    private static final Set<Integer> keysJustDown = ConcurrentHashMap.newKeySet();
    private static final Set<Integer> keysJustUp = ConcurrentHashMap.newKeySet();

    private static final int KEY_INPUT_BUFFER_SIZE = 256;

    public static boolean IsKeyDown(int keycode) {
        return keysDown.contains(keycode);
    }

    public static boolean IsKeyJustDown(int keycode) {
        return keysJustDown.contains(keycode);
    }

    public static boolean IsKeyJustUp(int keycode) {
        return keysJustDown.contains(keycode);
    }

    static void init() throws IOException, InterruptedException {
        //enable raw input
        if (Engine.isWindows) {
            new ProcessBuilder("cmd", "/c", "stty raw -echo").inheritIO().start().waitFor();
        } else {
            String[] cmd = {"/bin/sh", "-c", "stty raw -echo < /dev/tty"};
            new ProcessBuilder(cmd).start().waitFor();
        }

        System.out.print("\033[>27u");
        System.out.flush();
    }

    static void update() {
        //clear just arrays
        keysJustDown.clear();
        keysJustUp.clear();

        //parse inputs
        InputStream input = System.in;

        byte[] buffer = new byte[KEY_INPUT_BUFFER_SIZE];
        int bytesRead = 0;
        try {
            if (input.available() > 0) {
                bytesRead = input.read(buffer);
            }
        } catch (IOException e) {
            Debug.err(e.getMessage());
            throw new RuntimeException(e);
        }
        if (bytesRead <= 0) return;

        String seq = new String(buffer, 0, bytesRead);

        // Parse Kitty escape sequence: \033[ <key> ; <mods> : <event_type> u
        parseKittyEvent(seq);
    }

    static void clean() {
        //restore terminal
    }

    private static void parseKittyEvent(String seq) {
        if (!seq.startsWith("\033[") || !seq.endsWith("u")) return;

        String body = seq.substring(2, seq.length() - 1); // strip \033[ and u

        // Format: key;mods:event OR key:event OR key
        String[] parts = body.split(":");

        int eventType = 1;
        if (parts.length > 1) {
            try {
                eventType = Integer.parseInt(parts[1].split(";")[0]);
            } catch (NumberFormatException ignored) {
            }
        }

        if (eventType == 2) return;

        String keyAndMods = parts[0];
        String[] keyDetails = keyAndMods.split(";");
        String key = keyDetails[0];

        Debug.log(key);

        if (eventType == 3) {
            keysDown.remove(Integer.valueOf(key));
        } else {
            keysDown.add(Integer.valueOf(key));
        }
    }

    private static String escapeString(String str) {
        return str.replace("\033", "\\e");
    }

}
