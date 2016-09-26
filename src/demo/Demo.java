/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.lagodiuk.ga.Chromosome;
import com.lagodiuk.ga.Fitness;
import com.lagodiuk.ga.GeneticAlgorithm;
import com.lagodiuk.ga.IterartionListener;
import com.lagodiuk.ga.Population;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 *
 * @author Alvaro
 */
public class Demo {
    
    public static final int NODE_COUNT = 10;    
    public static final int MAX_INT = 10000;   
    public static final int ITERATIONS = 100;

    private static int[][] graph;    
    private static Point[] points;


    public static void main(String[] args) {
        
        loadGraph();
        
        Population<MyVector> population = createInitialPopulation(5);
        Fitness<MyVector, Double> fitness = new MyVectorFitness();
        GeneticAlgorithm<MyVector, Double> ga = new GeneticAlgorithm<MyVector, Double>(population, fitness);
        addListener(ga);
        ga.evolve(ITERATIONS);
    }
    
    private static void loadGraph(){
        points = new Point[]{
            new Point(0,0),
            new Point(6,0),
            new Point(1,4),
            new Point(2,4),
            new Point(3,6),
            new Point(10,1),
            new Point(6,6),
            new Point(3,7),
            new Point(4,10), 
            new Point(7,10)
        };
        
        graph = new int[NODE_COUNT][NODE_COUNT];
        for(int i=0;i<NODE_COUNT;i++){
            for(int j=0;j<NODE_COUNT;j++){                
                int dist = getDistance(i,j,MAX_INT);
                graph[i][j] = dist;
            }
        }
        
        setConnection(1,2);
        setConnection(1,3);
        setConnection(2,5);
        setConnection(5,6);
        setConnection(6,10);
        setConnection(5,8);
        setConnection(8,9);
        setConnection(9,10);
        setConnection(3,4);        
        setConnection(4,5);        
        setConnection(4,7);
        setConnection(7,10);        
    }
    
    private static void setConnection(int start, int end){
//        Because arrays are 0-based
        start -= 1;        
        end -= 1;

        int dist = getDistance(start,end,1);
        graph[start][end] = dist;
    }
    
    private static int getDistance(int start, int end, int factor){
        Point endPoint = points[end];
        return ((int) (points[start].distance(endPoint.x, endPoint.y) * 10)) * factor;
    }
    
    /**
     * The simplest strategy for creating initial population <br/>
     * in real life it could be more complex
     */
    private static Population<MyVector> createInitialPopulation(int populationSize) {
        Population<MyVector> population = new Population<MyVector>();
//        MyVector base = new MyVector();
        for (int i = 0; i < populationSize; i++) {
            // each member of initial population
            // is mutated clone of base chromosome
            MyVector chr = new MyVector();
            population.addChromosome(chr);
        }
        return population;
    }

    /**
     * After each iteration Genetic algorithm notifies listener
     */
    private static void addListener(GeneticAlgorithm<MyVector, Double> ga) {
        // just for pretty print
        System.out.println(String.format("%s\t%s\t%s", "iter", "fit", "chromosome"));

        // Lets add listener, which prints best chromosome after each iteration
        ga.addIterationListener(new IterartionListener<MyVector, Double>() {

            private final double threshold = 200;

            @Override
            public void update(GeneticAlgorithm<MyVector, Double> ga) {

                MyVector best = ga.getBest();
                double bestFit = ga.fitness(best);
                int iteration = ga.getIteration();

                // Listener prints best achieved solution
                System.out.println(String.format("%s\t%s\t%s", iteration, bestFit, best));

                // If fitness is satisfying - we can stop Genetic algorithm
                if (bestFit < this.threshold) {
                    ga.terminate();
                }
            }
        });
    }
    
    public static class MyVector implements Chromosome<MyVector>, Cloneable {

        private static final Random random = new Random();

        private final int RANGE = NODE_COUNT - 2;
        private final int[] vector = new int[]{1,2,3,4,5,6,7,8};
        public int len;

        public MyVector(){
            len = RANGE;
            for(int i = 0; i< 10; i++){
                randomSwap();
            }
        }
        
        public void randomSwap(){            
            int orig = random.nextInt(len);
            int dest = random.nextInt(RANGE);
            
            int temp = vector[orig];
            vector[orig] = vector[dest];
            vector[dest] = temp;
        }
        
        
        private void addLength(int value){
            int newLen = len + value;
            if ((newLen > RANGE) || (newLen < 1)){
                newLen += (value * -2);             
            }
            len = newLen;
        }
        
        private void splice(int pos){
            int temp = vector[pos];
            for(int i=0; i<vector.length - 1; i++){
                vector[i] = vector[i+1];
            }
            vector[vector.length - 1] = temp;
        }
        
        @Override
        public MyVector mutate() {
            MyVector result = this.clone();
            
//            Add or Decrease length
            int lenToAdd = random.nextInt(2) - random.nextInt(2);
            result.addLength(lenToAdd);
            
//            Splice 1 Gen
            if (random.nextInt(100) > 50){
                result.splice(random.nextInt(len));
            } else{
                result.insertGen(random.nextInt(len),random.nextInt(RANGE));
            }
//            Change a Gen
            result.randomSwap();            

            return result;
        }

        
        @Override
        public List<MyVector> crossover(MyVector other) {
            MyVector clone1 = (this.len <= other.len)? this.clone():other.clone();
            MyVector clone2 = (this.len <= other.len)? other.clone():this.clone();
//
//            // one point crossover
            int index = random.nextInt(clone1.len);
            for (int i = index; i < clone2.len; i++) {
                int tmp = clone1.vector[i];
            } 

            return Arrays.asList(clone1, clone2);
        }
        
        
        private void insertGen(int pos, int value){
            boolean isHere = false;
            
            for (int i=0; i< pos; i++){
                if (vector[i] == value){
                    isHere = true;
                    break;
                }
            }
            
            if (isHere){
//                Do nothing
            } else {
                for (int i=pos; i< vector.length; i++){
                    if (vector[i] == value){
                        vector[i] = vector[pos];
                        break;
                    }                
                }
                vector[pos] = value;
            }
            
        }

        @Override
        protected MyVector clone() {
            MyVector clone = new MyVector();
            clone.len = this.len;
            System.arraycopy(this.vector, 0, clone.vector, 0, this.vector.length);
            return clone;
        }

        public int[] getVector(){
            int[] v = new int[vector.length];
            for (int i=0; i<len; i++){
                v[i] = vector[i];
            }
            return v;
        }
        
        public int[] getVectorString(){
            int[] v = new int[RANGE+2];
            v[0] = 1;
            for (int i=0; i<len; i++){
                v[i+1] = vector[i] + 1;
            }
            v[len+1] = 10;
            return v;
        }
        
        

        @Override
        public String toString() {
            return Arrays.toString(getVectorString());
        }
    }

    public static class MyVectorFitness implements Fitness<MyVector, Double> {

        @Override
        public Double calculate(MyVector chromosome) {
            int[] v = chromosome.getVector();
            
            int acc = 0; 
            int lastPoint = 0;            
            for (int i = 0; i < chromosome.len; i++) { 
                int newPoint = 0;
                try{              
                    newPoint = v[i];   
                    acc += graph[lastPoint][newPoint];
                    lastPoint = newPoint; 
                } catch(Exception e){
                    System.out.println("lastPoint: " + lastPoint + "  i: " + i);                    
                }                  
            }
            acc += graph[lastPoint][NODE_COUNT-1];
            
            return new Double(acc);
        }
    }
}