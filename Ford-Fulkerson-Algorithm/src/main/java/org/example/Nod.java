package org.example;

public class Nod {
    int id;
    int x, y;
    static final int RADIUS = 20;
    static final int CLICK_RADIUS = 40; // diametrul nodului

    public Nod(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    //verifica daca clicku ul este in interiorul nodului
    public boolean contains(int px, int py) {
        return Math.sqrt((px - x) * (px - x) + (py - y) * (py - y)) <= CLICK_RADIUS;
    }
}

