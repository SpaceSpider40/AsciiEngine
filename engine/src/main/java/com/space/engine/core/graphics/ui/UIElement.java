package com.space.engine.core.graphics.ui;

public abstract class UIElement {
    protected int x, y;
    protected int width, height;

    protected byte layer;

    protected char[] chars;

    protected UIElement(
            int x,
            int y,
            int width,
            int height
    ){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        chars = new char[width * height];
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public char[] getChars() {
        return chars;
    }

    public abstract void update(double deltaTime);

    protected void put(int x, int y, char c){
        if (x < 0 || y < 0 || x >= width || y >= height) return;

        chars[x + y * width] = c;
    }

    protected void putText(int x, int y, String text){
        for (int i = 0; i < text.length(); i++) {
            put(x + i, y, text.charAt(i));
        }
    }
}
