package org.example;

public class Edge {
    private int targetNodeId;
    private int length;

    public Edge(int targetNodeId, int length) {
        this.targetNodeId = targetNodeId;
        this.length = length;
    }

    public int getTargetNodeId() { return targetNodeId; }
    public int getLength() { return length; }
}
