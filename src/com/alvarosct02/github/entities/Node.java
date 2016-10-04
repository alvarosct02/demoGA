package com.alvarosct02.github.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Alvaro on 10/3/2016.
 */

public class Node {
    private int id;
    private String code;
    private String city = "";
    private String country = "";
    private String alias = "";
    private int continentId;
    private String continent = "";
    private int timezone;

    private List<Connection> connections;

    public Node(String line, int continentId, String continent) {

        String[] nodeValues = line.split("\t");

        this.id = Integer.parseInt(nodeValues[0].trim());
        this.code = nodeValues[1].trim();
        this.city = nodeValues[2].trim();
        this.country = nodeValues[3].trim();
        this.alias = nodeValues[4].trim();
        this.continent = continent;
        this.continentId = continentId;
        this.connections = new ArrayList<>();
    }

    public void createConnection(Node end, int startMin, int endMin){
        Connection connection  = new Connection(end, startMin, endMin);
        this.getConnections().add(connection);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", getCode(), getCity());
    }


    public String getCode() {
        return code;
    }

    public int getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getAlias() {
        return alias;
    }

    public int getContinentId() {
        return continentId;
    }

    public String getContinent() {
        return continent;
    }

    public int getTimezone() {
        return timezone;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public Connection pickNextConnection(Random random){
        int index = random.nextInt(getConnections().size());
        return getConnections().get(index);
    }

    public Connection getConnectionTo(Node node){
        for (Connection connection: getConnections()) {
            if (connection.getEndNode().equals(node)){
                return connection;
            }
        }
        return null;
    }
}
