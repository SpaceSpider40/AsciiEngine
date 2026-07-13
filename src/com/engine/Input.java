package com.engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public final class Input implements IUpdate {
    private static final boolean[] keys = new boolean[256];

    static final KeyAdapter KeyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            keys[e.getKeyCode()] = true;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            keys[e.getKeyCode()] = false;
        }
    };

    private Input() {

    }

    public static boolean isKeyDown(int keyCode) {
        return keys[keyCode];
    }

    @Override
    public void update(double delta) {

    }
}
