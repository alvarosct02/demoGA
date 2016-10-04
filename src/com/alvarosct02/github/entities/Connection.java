package com.alvarosct02.github.entities;

/**
 * Created by Alvaro on 10/3/2016.
 */

public class Connection {

    //        Time in minutes, always positive integer
    private int startMin;
    private int endMin;
    private Node endNode;

    public Connection(Node node, int startMin, int endMin) {
        this.endNode = node;
        this.startMin = startMin;
        this.endMin = endMin;
    }

    /**
     * @return Travel time in minutes
     */
    public int getTravelTime() {
        int travelMinutes = getEndMin() - getStartMin();
        if (travelMinutes < 0) travelMinutes += 24 * 60;
        return travelMinutes;
    }

    public Node getEndNode() {
        return endNode;
    }

    public int getStartMin() {
        return startMin;
    }

    public int getEndMin() {
        return endMin;
    }
}