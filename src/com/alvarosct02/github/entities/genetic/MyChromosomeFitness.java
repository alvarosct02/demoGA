package com.alvarosct02.github.entities.genetic;

import com.lagodiuk.ga.Fitness;

/**
 * Created by Alvaro on 10/3/2016.
 */

public class MyChromosomeFitness implements Fitness<MyChromosome, Double> {

    @Override
    public Double calculate(MyChromosome chromosome) {
        return new Double(chromosome.getTotalTime());
    }
}