package org.example;

import java.util.Objects;

public class Arc {
    int from, to;
    int capacity;
    int flow;

    public Arc(int from, int to, int capacity, int flow) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = flow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Arc)) return false;
        Arc arc = (Arc) o;
        return from == arc.from && to == arc.to;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}

