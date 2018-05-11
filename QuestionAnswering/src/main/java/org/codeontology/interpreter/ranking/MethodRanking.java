package org.codeontology.interpreter.ranking;

import org.codeontology.individuals.MethodIndividual;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MethodRanking {

    private List<RankedMethod> ranking;

    public MethodRanking(MethodRanker ranker, List<RankedMethod> methods) {
        ranking = new ArrayList<>(methods);
        ranking.sort(ranker);
    }

    public List<RankedMethod> asList() {
        return ranking;
    }

    public List<MethodIndividual> getMethods() {
        return ranking.stream()
                .map(RankedMethod::getMethod)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        int size = ranking.size();
        int n = 4;

        String dots = "\n...";
        if (size < n) {
            dots = "";
            n = size;
        }

        return ranking.subList(0, n).stream()
                .map(RankedMethod::toString)
                .collect(Collectors.joining("\n")) + dots;

    }

    public RankedMethod top() {
        return ranking.get(0);
    }

    public MethodIndividual topMethod() {
        return top().getMethod();
    }

    public List<RankedMethod> top(int n) {
        if (n > ranking.size()) {
            return asList();
        }
        return ranking.subList(0, n);
    }

    public List<MethodIndividual> topMethods(int n) {
        return top(n).stream()
                .map(RankedMethod::getMethod)
                .collect(Collectors.toList());
    }

    public double getTopScore() {
        return top().getScore();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodRanking)) return false;

        MethodRanking that = (MethodRanking) o;

        return ranking.equals(that.ranking);
    }

    @Override
    public int hashCode() {
        return ranking.hashCode();
    }
}
