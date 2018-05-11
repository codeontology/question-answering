package org.codeontology.interpreter.dependencies.tagged;

import org.codeontology.Settings;
import org.codeontology.individuals.TypeIndividual;
import org.codeontology.interpreter.dependencies.DependencyGraph;
import org.codeontology.interpreter.dependencies.DependencyGraphNode;
import org.codeontology.interpreter.execution.ExecutionTree;
import org.codeontology.interpreter.preprocessing.ParameterizedString;
import org.codeontology.interpreter.ranking.MethodRanking;
import org.codeontology.interpreter.ranking.RankedMethod;
import org.codeontology.query.TypeFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TaggedDependencyGraph {
    private DependencyGraph dependencyGraph;
    private Map<DependencyGraphNode, MethodRanking> rankingMap;
    private Map<DependencyGraphNode, Set<TypeIndividual>> typeMap;

    public TaggedDependencyGraph(DependencyGraph dependencyGraph) {
        if (dependencyGraph == null) {
            throw new IllegalArgumentException();
        }
        this.dependencyGraph = dependencyGraph;
        DependencyGraphTagger tagger = new DependencyGraphTagger(dependencyGraph);
        tagger.tag();
        this.rankingMap = tagger.getRankingMap();
        this.typeMap = tagger.getTypeMap();
    }

    public Set<TypeIndividual> getTypeTag(DependencyGraphNode node) {
        return typeMap.get(node);
    }

    public MethodRanking getRankingTag(DependencyGraphNode node) {
        return rankingMap.get(node);
    }

    private Map<DependencyGraphNode, Integer> getInitialIndices() {
        Map<DependencyGraphNode, Integer> indices = new HashMap<>();
        rankingMap.keySet().forEach(node -> indices.put(node, 0));
        return indices;
    }

    public ExecutionTree findBestFeasibleTree() throws NoFeasibleTreeException {
        return findBestFeasibleTree(getInitialIndices());
    }

    public ExecutionTree findBestFeasibleTree(Class<?> returnType) throws NoFeasibleTreeException {
        if (returnType.equals(Object.class)) {
            return findBestFeasibleTree();
        }
        Map<DependencyGraphNode, Integer> indices = getInitialIndices();
        TypeIndividual expectedType = TypeFactory.getInstance().getTypeByReflection(returnType);
        return findBestFeasibleTree(indices, expectedType);
    }

    private ExecutionTree findBestFeasibleTree(Map<DependencyGraphNode, Integer> indices, TypeIndividual expectedType) throws NoFeasibleTreeException {
        ExecutionTree tree = findBestFeasibleTree(indices);
        TypeIndividual actualType = tree.getReturnType();
        if (actualType.getImplicitlyAssignableTypes().contains(expectedType)) {
            return tree;
        }
        updateIndices(indices);
        return findBestFeasibleTree(indices, expectedType);
    }

    private ExecutionTree findBestFeasibleTree(Map<DependencyGraphNode, Integer> indices) throws NoFeasibleTreeException {
        try {
            Map<DependencyGraphNode, RankedMethod> methodMap = new HashMap<>();
            Set<Map.Entry<DependencyGraphNode, MethodRanking>> entries = rankingMap.entrySet();

            entries.forEach(entry -> {
                DependencyGraphNode key = entry.getKey();
                MethodRanking ranking = entry.getValue();
                Integer index = indices.get(key);
                RankedMethod method = ranking.asList().get(index);
                methodMap.put(key, method);

            });

            ExecutionTree executionTree = new ExecutionTree(this, methodMap);

            if (executionTree.isFeasible()) {
                return executionTree;
            }

            updateIndices(indices);
            return findBestFeasibleTree(indices);
        } catch (NoFeasibleTreeException e) {
            throw e;
        } catch (Exception e) {
            throw new NoFeasibleTreeException();
        }
    }

    private void updateIndices(Map<DependencyGraphNode, Integer> indices) throws NoFeasibleTreeException {
        DependencyGraphNode bestNode = null;
        double bestDifference = 1;

        int threshold = Settings.RANKING_SIZE;
        for (DependencyGraphNode node : indices.keySet()) {
            Integer index = indices.get(node);
            List<RankedMethod> methods = rankingMap.get(node).asList();
            RankedMethod method = methods.get(index);
            if (index < methods.size() - 1 && index < threshold) {
                double currentScore = method.getScore();
                double nextScore = methods.get(index + 1).getScore();
                double difference = currentScore - nextScore;
                if (difference <= bestDifference) {
                    bestDifference = difference;
                    bestNode = node;
                }
            }
        }

        if (bestNode == null) {
            throw new NoFeasibleTreeException();
        }

        int index = indices.get(bestNode);
        indices.put(bestNode, index + 1);
    }

    public List<DependencyGraphNode> getRoots() {
        return dependencyGraph.getRoots();
    }

    public ParameterizedString getCommand() {
        return dependencyGraph.getCommand();
    }

    @Override
    public String toString() {
        return dependencyGraph.toString() + "\n\n" + rankingMap;
    }
}
