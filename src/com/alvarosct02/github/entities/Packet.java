package com.alvarosct02.github.entities;

import com.alvarosct02.github.Demo;

/**
 * Created by Alvaro on 10/4/2016.
 */
public class Packet {

    private int startTime;
    private int maxTime;
    private String startCity;
    private String endCity;

    public Packet(String startCity, String endCity, int startTime) {

        this.startCity = startCity;
        this.endCity = endCity;
        this.startTime = startTime;

        Node startNode = Demo.getInstance().findNode(startCity);
        Node endNode = Demo.getInstance().findNode(endCity);

        this.maxTime = 12 * 60;
        if (startNode.getContinentId() != endNode.getContinentId()) {
            this.maxTime = 24 * 60;
        }
    }

    public int getStartTime() {
        return startTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public String getStartCity() {
        return startCity;
    }

    public String getEndCity() {
        return endCity;
    }
}
