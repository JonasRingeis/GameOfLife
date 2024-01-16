package org.example;

public class Vector2 {
    public int x;
    public int y;

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Vector2 add(Vector2 vectorToAdd) {
        return new Vector2(x + vectorToAdd.x, y + vectorToAdd.y);
    }

    @Override
    public String toString() {
        return String.format("Vector2(%s, %s)", x, y);
    }
}