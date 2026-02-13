package org.example;

public class Node {
    private int id;
    private int x, y;
    private int screenX, screenY; // coord ecran

    public Node(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }

    public int getScreenX() { return screenX; }
    public void setScreenX(int screenX) { this.screenX = screenX; }

    public int getScreenY() { return screenY; }
    public void setScreenY(int screenY) { this.screenY = screenY; }
}