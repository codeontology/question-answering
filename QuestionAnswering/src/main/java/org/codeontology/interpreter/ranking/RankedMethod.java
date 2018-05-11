package org.codeontology.interpreter.ranking;

import org.codeontology.individuals.MethodIndividual;

public class RankedMethod {
    private MethodIndividual method;
    private double score;

    public RankedMethod(MethodIndividual method, double score) {
        this.method = method;
        this.score = score;
    }

    public MethodIndividual getMethod() {
        return method;
    }

    public double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "(" + method + ", " + score + ")";
    }
}
