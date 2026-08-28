package com.space.engine.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class Input {

    public static final int KEY_A = 97;
    public static final int KEY_B = 98;
    public static final int KEY_C = 99;
    public static final int KEY_D = 100;
    public static final int KEY_E = 101;
    public static final int KEY_F = 102;
    public static final int KEY_G = 103;
    public static final int KEY_H = 104;
    public static final int KEY_I = 105;
    public static final int KEY_J = 106;
    public static final int KEY_K = 107;
    public static final int KEY_L = 108;
    public static final int KEY_M = 109;
    public static final int KEY_N = 110;
    public static final int KEY_O = 111;
    public static final int KEY_P = 112;
    public static final int KEY_Q = 113;
    public static final int KEY_R = 114;
    public static final int KEY_S = 115;
    public static final int KEY_T = 116;
    public static final int KEY_U = 117;
    public static final int KEY_V = 118;
    public static final int KEY_W = 119;
    public static final int KEY_X = 120;
    public static final int KEY_Y = 121;
    public static final int KEY_Z = 122;

    private static final Set<Integer> keysDown     = ConcurrentHashMap.newKeySet();
    private static final Set<Integer> keysJustDown = ConcurrentHashMap.newKeySet();
    private static final Set<Integer> keysJustUp   = ConcurrentHashMap.newKeySet();

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
        if (EngineImpl.isWindows) {
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

        byte[] buffer    = new byte[KEY_INPUT_BUFFER_SIZE];
        int    bytesRead = 0;
        try {
            if (input.available() > 0) {
                bytesRead = input.read(buffer);
            }
        } catch (IOException e) {
            Debug.err(e.getMessage());
            throw new RuntimeException(e);
        }
        if (bytesRead <= 0) return;

        String[] sequences = new String(buffer, 0, bytesRead).split("(?=\033)");
        for (String seq : sequences) {
            parseKittyEvent(seq);
        }
    }

    static void clean() {
        //restore terminal
        try {
            System.out.print("\033[<u");
            System.out.flush();

            if (EngineImpl.isWindows){
                new ProcessBuilder("cmd", "/c", "stty cooked echo").inheritIO().start().waitFor();
            }else{
                String[] cmd = {"/bin/sh", "-c", "stty cooked echo < /dev/tty"};

                new ProcessBuilder(cmd).start().waitFor();
            }
        }catch (Exception ignored){}
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

        String   keyAndMods = parts[0];
        String[] keyDetails = keyAndMods.split(";");

        int keyCode;
        try {
            keyCode = Integer.parseInt(keyDetails[0]);
        } catch (NumberFormatException e) {
            return;
        }

        if (eventType == 3) {
            if (keysDown.remove(keyCode)) {
                keysJustUp.add(keyCode);
            }
        }
        if (eventType == 1) {
            boolean isInitialPress = keysDown.add(keyCode);

            if (isInitialPress) {
                keysJustDown.add(keyCode);
            }
        }
    }
}
