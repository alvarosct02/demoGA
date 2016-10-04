/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alvarosct02.github;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alvarosct02.github.entities.genetic.MyChromosome;
import com.alvarosct02.github.entities.genetic.MyChromosomeFitness;
import com.alvarosct02.github.entities.Packet;
import com.lagodiuk.ga.Fitness;
import com.lagodiuk.ga.GeneticAlgorithm;
import com.lagodiuk.ga.IterartionListener;
import com.lagodiuk.ga.Population;
import com.alvarosct02.github.entities.Node;

/**
 * @author Alvaro
 */
public class Demo {

    private static Demo instance;

    public static Demo getInstance() {
        if (instance == null) {
            instance = new Demo();
        }
        return instance;
    }

    public static void main(String[] args) {

        long startTime1 = System.nanoTime();


//        Loading the Data
        Demo demo = getInstance();
//        Loading the Data


        long endTime1 = System.nanoTime();
        float durationMilis1 = (float) (endTime1 - startTime1) / 1000000;
        System.out.println(String.format("Tiempo de Carga: %.3f miliseconds", durationMilis1));



        long startTime2 = System.nanoTime();


//        Running the Algorithm
        try {
            demo.readPackets();
        } catch (Exception e) {
            System.out.println("An Error has occurred while reading the packets");
            System.out.println(e.getMessage());
        }
//        Running the Algorithm

        long endTime2 = System.nanoTime();
        float durationMilis2 = (float) (endTime2 - startTime2) / 1000000;
        System.out.println(String.format("Tiempo de ejecución: %.3f miliseconds", durationMilis2));

        float durationMilis3 = (float) (endTime2 - startTime1) / 1000000;
        System.out.println(String.format("Tiempo total: %.3f miliseconds", durationMilis3));


        int mb = 1024 * 1024;

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        System.out.println("Used Memory:"
                + (runtime.totalMemory() - runtime.freeMemory()) / mb);

    }


    public static final boolean LOGGING = false;
    public static final int POP_SIZE = 10;
    public static final int ITERATIONS = 50;

    private Map<String, Node> nodes;

    public Demo() {

        nodes = new HashMap<>();

//        Loading Data
        try {
            readNodes();
            readConnections();

        } catch (Exception e) {
            System.out.println("An Error has occurred");
            System.out.println(e.getMessage());
        }
    }

    private void runAlgorithm(Packet packet) {
        Population<MyChromosome> population = createInitialPopulation(POP_SIZE, packet);
        Fitness<MyChromosome, Double> fitness = new MyChromosomeFitness();
        GeneticAlgorithm<MyChromosome, Double> ga = new GeneticAlgorithm<>(population, fitness);
        addListener(ga);
        ga.evolve(ITERATIONS);
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

//    ###############################
//    TODO:    LOADING DATA
//    ###############################

    private void readConnections() throws Exception {

        int counter = 0;

        BufferedReader in = new BufferedReader(new FileReader("_plan_vuelo.txt"));

        String line;
        while ((line = in.readLine()) != null) {
            String[] a = line.split("-");

            if (a.length == 0) continue;

//            Find Starting Node
            Node startNode = findNode(a[0]);
            if (startNode == null) continue;

//            Find Ending Node
            Node endNode = findNode(a[1]);
            if (endNode == null) continue;

//            Assign Connection
            startNode.createConnection(endNode, getMinutes(a[2], "HH:mm"), getMinutes(a[3], "HH:mm"));
            counter += 1;

        }
        in.close();
        System.out.println(String.format("Se han agregado %d conexiones", counter));
    }

    private void readPackets() throws Exception {

        BufferedReader in = new BufferedReader(new FileReader("_paquetes.txt"));

        String line;
        while ((line = in.readLine()) != null) {
            String[] a = line.split("-");

            if (a.length == 0) continue;

            int startTimeMinutes = getMinutes(a[2], "HH:mm");

//            Assign Connection
            Packet packet = new Packet(a[0], a[1], startTimeMinutes);
            runAlgorithm(packet);


        }
        in.close();
    }

    private void readNodes() throws Exception {

        int counter = 0;
        BufferedReader in = new BufferedReader(new FileReader("_aeropuertos.OACI.txt"));

//        Ignore First Line
        in.readLine();

        int continentId = -1;

        String line;
        String continent = "";
        while ((line = in.readLine()) != null) {
            String[] a = line.split("\t");

            if (a.length == 0) continue;

            if (a[0].isEmpty() && !a[1].isEmpty()) {
                continentId += 1;
                continent = a[1];
                continue;
            }

//            Add to Nodes
            Node newNode = new Node(line, continentId, continent);
            nodes.put(newNode.getCode(), newNode);
            counter++;

        }
        in.close();
        System.out.println(String.format("Se han agregado %d nodos", counter));
    }

    private int getMinutes(String timeString, String format) {
        Calendar time = parseFromString(timeString, format);
        int hours = time.get(Calendar.HOUR_OF_DAY);
        int minutes = time.get(Calendar.MINUTE);

        return hours * 60 + minutes;
    }

    private Calendar parseFromString(String date, String format) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            cal.setTime(sdf.parse(date));// all done
        } catch (Exception e) {

        }
        return cal;
    }

    public Node findNode(String key) {
        return getNodes().get(key);
    }

    private void printNodes() {
        for (Map.Entry<String, Node> entry : getNodes().entrySet()) {
            Node node = entry.getValue();
            System.out.println(node);
        }
    }


//    #########################################
//    TODO:    CREATE INITIAL POPULATION
//    #########################################

    private Population<MyChromosome> createInitialPopulation(int populationSize, Packet packet) {
        Population<MyChromosome> population = new Population<>();

        Node startNode = findNode(packet.getStartCity());
        Node endNode = findNode(packet.getEndCity());

        int i = 0;
        int counter = 0;
        while (i < populationSize) {
            MyChromosome chr = new MyChromosome(startNode, endNode, packet.getStartTime(), packet.getMaxTime());
            if (chr.createRandomSolution()) {
                System.out.println(chr);

                population.addChromosome(chr);
                i += 1;
            }
            counter += 1;
        }
        System.out.println(String.format("Se han realizado %d intentos en la creacion de poblacion inicial", counter));
        return population;
    }


//    #####################################
//    TODO:    JUST TO PRETTY PRINT
//    #####################################

    /**
     * After each iteration Genetic algorithm notifies listener
     */
    private void addListener(GeneticAlgorithm<MyChromosome, Double> ga) {
        // just for pretty print
//        System.out.println(String.format("%s\t%s\t%s", "iter", "fit", "chromosome"));
//
//        // Lets add listener, which prints best chromosome after each iteration
        ga.addIterationListener(new IterartionListener<MyChromosome, Double>() {

            private final double threshold = 10;

            @Override
            public void update(GeneticAlgorithm<MyChromosome, Double> ga) {

                MyChromosome best = ga.getBest();
                double bestFit = ga.fitness(best);
                int iteration = ga.getIteration();

                // Listener prints best achieved solution
                System.out.println(String.format("%s\t%s\t%s", iteration, bestFit / 10, best));

                // If fitness is satisfying - we can stop Genetic algorithm
                if (bestFit < this.threshold) {
                    ga.terminate();
                }
            }
        });
    }
}