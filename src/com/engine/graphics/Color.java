package com.engine.graphics;

import com.engine.math.Vector3;

//todo: improve by storing values as packed int;
public class Color {
    public static final Color RED    = new Color(255, 0, 0);
    public static final Color GREEN  = new Color(0, 255, 0);
    public static final Color BLUE   = new Color(0, 0, 255);
    public static final Color YELLOW = new Color(255, 255, 0);
    public static final Color WHITE  = new Color(255, 255, 255);
    public static final Color Black  = new Color(0, 0, 0);

    private short r;
    private short g;
    private short b;

    public Color() {
        this(0, 0, 0);
    }

    public Color(int packed) {
        this(
                (short) unpackR(packed),
                (short) unpackG(packed),
                (short) unpackB(packed)
        );
    }

    public Color(int r, int g, int b) {
        this(
                (short) r,
                (short) g,
                (short) b
        );
    }

    public Color(short r, short g, short b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public short getR() {
        return r;
    }

    public void setR(short r) {
        this.r = (short) Math.clamp(r, 0, 255);
    }

    public short getG() {
        return g;
    }

    public void setG(short g) {
        this.g = (short) Math.clamp(g, 0, 255);
    }

    public short getB() {
        return b;
    }

    public void setB(short b) {
        this.b = (short) Math.clamp(b, 0, 255);
    }

    public Vector3 asVector() {
        return new Vector3(r, g, b);
    }

    /**
     * Packs each color value into singular int
     * "RRRRRRRRGGGGGGGGBBBBBBBB"
     *
     * @return int packed int color
     */
    public int pack() {
        return (r << 16) | (g << 8) | b;
    }

    /**
     * Unpacking method running on primitive values.
     * Used in renderer where object creation is too slow;
     *
     * @return int
     */
    public static int unpackR(int packedColor) {
        return (packedColor >> 16) & 0xFF;
    }

    /**
     * Unpacking method running on primitive values.
     * Used in renderer where object creation is too slow;
     *
     * @return int
     */
    public static int unpackG(int packedColor) {
        return (packedColor >> 8) & 0xFF;
    }

    /**
     * Unpacking method running on primitive values.
     * Used in renderer where object creation is too slow;
     *
     * @return int
     */
    public static int unpackB(int packedColor) {
        return packedColor & 0xFF;
    }

    public static Color unpack(int packedColor) {
        return new Color(
                unpackR(packedColor),
                unpackG(packedColor),
                unpackB(packedColor)
        );
    }
}
