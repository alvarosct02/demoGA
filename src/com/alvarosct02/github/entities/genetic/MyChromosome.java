package com.alvarosct02.github.entities.genetic;

import com.alvarosct02.github.Constants;
import com.alvarosct02.github.Demo;
import com.alvarosct02.github.entities.Connection;
import com.alvarosct02.github.entities.Node;
import com.lagodiuk.ga.Chromosome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Alvaro on 10/3/2016.
 */

public class MyChromosome implements Chromosome<MyChromosome>, Cloneable {

    private static final Random random = new Random();

    private List<Node> vector;
    private int maxTime;
    private Node start;
    private Node end;
    private int startTime;
    private int totalTime;

    public MyChromosome(Node start, Node end, int startTimeMinutes, int maxTime) {
        this.vector = new ArrayList<>();
        this.maxTime = maxTime;
        this.start = start;
        this.startTime = startTimeMinutes;
        this.end = end;
        this.end = end;
    }

    public boolean createRandomSolution() {
        int currentTime = startTime;
        totalTime = 0;
        Node current = start;

//        Add Starting Node
        vector.add(current);

        while (!current.getConnections().isEmpty()) {

            Connection connection = current.pickNextConnection(random);

//            If Connection StartMin is less than currentTime it means you will be taken the flight the next day
            int waitingMinutes = connection.getStartMin() - currentTime;
            if (waitingMinutes < 0) waitingMinutes += Constants.DAY_MINUTES;

            totalTime += waitingMinutes;
            totalTime += connection.getTravelTime();

//            To avoid passing the given time (weight)
            if (totalTime > maxTime) {
                if (Demo.LOGGING) System.out.println("WEIGHT > " + this);
                break;
            }

            currentTime = connection.getEndMin();
            current = connection.getEndNode();

//            To avoid going to a visited node
            if (vector.contains(current)) {
                if (Demo.LOGGING) System.out.println("REPEAT > " + this);
                break;
            }

//            Add New Node
            vector.add(current);

//           if it find a valid solution. SUCCESS!
            if (current.equals(end)) {
                return true;
            }
        }
        return false;
    }

    public int refreshTotalTime() {
        int currentTime = startTime;
        totalTime = 0;

        Node current = start;
        Node next;

        for (int i = 1; i < vector.size(); i++) {
            next = vector.get(i);
            Connection connection = current.getConnectionTo(next);

//            If Path does NOT exist
            if (connection == null){
                totalTime = Constants.PENALTY;
                break;
            }

//            If Connection StartMin is less than currentTime it means you will be taken the flight the next day
            int waitingMinutes = connection.getStartMin() - currentTime;
            if (waitingMinutes < 0) waitingMinutes += Constants.DAY_MINUTES;

            totalTime += waitingMinutes;
            totalTime += connection.getTravelTime();

//            To avoid passing the given time (weight)
            if (totalTime > maxTime) {
                totalTime = Constants.PENALTY;
                break;
            }

            currentTime = connection.getEndMin();
            current = next;
        }

        return totalTime;
    }

    @Override
    public MyChromosome mutate() {
        MyChromosome result = this.clone();
        return result;
    }


    @Override
    public List<MyChromosome> crossover(MyChromosome other) {

        MyChromosome clone1 = this.clone();
        MyChromosome clone2 = this.clone();

        int crossPointIndex = getRandomCrossPoint(clone1, clone2);
        Node temp = clone1.vector.get(crossPointIndex);
        clone1.vector.set(crossPointIndex, clone2.vector.get(crossPointIndex));
        clone2.vector.set(crossPointIndex, temp);

        clone1.refreshTotalTime();
        clone2.refreshTotalTime();

        return Arrays.asList(clone1, clone2);
    }

    private int getRandomCrossPoint(MyChromosome c1, MyChromosome c2){
        return random.nextInt(Math.min(c1.getVectorLength() ,c2.getVectorLength()));
    }

    private List<Integer> genMergedList(List<Integer> list1, List<Integer> list2, int pos1, int pos2) {
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i <= pos1; i++) {
            list.add(list1.get(i));
        }

        for (int i = pos2 + 1; i < list2.size(); i++) {
            list.add(list2.get(i));
        }

        return list;
    }

    private List<Integer> getCommonNodes(List<Integer> list1, List<Integer> list2) {
        List<Integer> common = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            int val = list1.get(i);
            if (list2.contains(val)) {
                common.add(val);
            }
        }
        return common;
    }


    @Override
    protected MyChromosome clone() {
        MyChromosome clone = new MyChromosome(start, end, startTime, totalTime);
        clone.vector = vector;
        clone.totalTime = getTotalTime();
        return clone;
    }

    @Override
    public String toString() {
        String out = "[" + vector.get(0).getId();
        for (int i = 1; i < vector.size(); i++) {
            out += "," + vector.get(i).getId();
        }
        out += "]";

        out += "   >>   " + getTotalTime();
        return out;

    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getVectorLength(){
        return vector.size();
    }
}
