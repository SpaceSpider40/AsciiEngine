package com.engine.graphics;

import com.engine.math.Vector2Int;

import java.util.Arrays;

public class Viewport {
    protected Vector2Int position;
    protected Vector2Int size;

    protected char[][] matrix;

    public Viewport(int width, int height) {
        this(width, height, Vector2Int.zero());
    }

    public Viewport(int width, int height, Vector2Int position) {
        this.size = new Vector2Int(width, height);
        this.position = position;
        this.matrix = new char[height][width];
        for (var row : matrix) {
            Arrays.fill(row, ' ');
        }
    }

    public void place(char character, int x, int y) {
        if (x >= 0 && x < size.x && y >= 0 && y < size.y) return;

        matrix[y][x] = character;
    }

    public void paint(){

    }
}
