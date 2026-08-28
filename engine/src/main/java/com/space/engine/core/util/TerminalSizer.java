package com.space.engine.core.util;

import com.space.engine.core.Debug;

import java.io.InputStream;

public final class TerminalSizer {
    public static final int DEFAULT_WIDTH  = 80;
    public static final int DEFAULT_HEIGHT = 24;

    public record TerminalSize(int width, int height) {
    }

    public static TerminalSize getTerminalSize() {
        try {

            System.out.print("\033[9999;9999H\033[6n");
            System.out.flush();

            InputStream   in = System.in;
            StringBuilder sb = new StringBuilder();
            int           c;

            while ((c = in.read()) != -1) {
                if (c == 'R') break;
                if (c != '\033' && c != '[') {
                    sb.append((char) c);
                }
            }

            Debug.log(sb.toString());

            String[] parts = sb.toString().split(";");

            return new TerminalSize(
                    Integer.parseInt(parts[1]), //rows
                    Integer.parseInt(parts[0])  //cols
            );

        } catch (Exception e) {
            Debug.warn("Failed to detect terminal size. Falling back to default size");
            return new TerminalSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        }
    }
}
