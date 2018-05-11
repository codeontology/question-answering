package org.codeontology.interpreter.dependencies.tagged;

import org.codeontology.Settings;
import org.codeontology.individuals.MethodIndividual;
import org.codeontology.individuals.TypeIndividual;
import org.codeontology.interpreter.NaturalLanguageCommand;
import org.codeontology.interpreter.dependencies.DependencyGraph;
import org.codeontology.interpreter.dependencies.DependencyGraphNode;
import org.codeontology.interpreter.ranking.MethodRanker;
import org.codeontology.interpreter.ranking.MethodRanking;
import org.codeontology.interpreter.preprocessing.ParameterizedString;
import org.codeontology.interpreter.ranking.RankingCache;
import org.codeontology.interpreter.ranking.RankingUnderThresholdException;
import org.codeontology.query.MethodFactory;
import org.codeontology.query.TypeFactory;

import java.util.*;
import java.util.stream.Collectors;

public class DependencyGraphTagger {

    private DependencyGraph dependencyGraph;
    private Map<DependencyGraphNode, Set<TypeIndividual>> typeMap;
    private Map<DependencyGraphNode, MethodRanking> rankingMap;

    public DependencyGraphTagger(DependencyGraph dependencyGraph) {
        if (dependencyGraph == null) {
            throw new IllegalArgumentException();
        }
        this.dependencyGraph = dependencyGraph;
        typeMap = new HashMap<>();
        rankingMap = new HashMap<>();
    }

    public Map<DependencyGraphNode, MethodRanking> tag() {
        List<DependencyGraphNode> roots = dependencyGraph.getRoots();
        roots.forEach(this::tagSubTree);
        return rankingMap;
    }

    public Map<DependencyGraphNode, Set<TypeIndividual>> getTypeMap() {
        return typeMap;
    }

    public Map<DependencyGraphNode, MethodRanking> getRankingMap() {
        return rankingMap;
    }

    private void tagSubTree(DependencyGraphNode node) {
        if (node.isLeaf()) {
            tagLeaf(node);
        } else {
            Set<DependencyGraphNode> children = node.getDependencies();
            children.forEach(this::tagSubTree);
            tagMethod(node);
        }

    }

    private void tagLeaf(DependencyGraphNode leaf) {
        String label = leaf.getLabel();
        if (dependencyGraph.getCommand().hasArgument(label)) {
            tagLeafArgument(leaf);
        } else {
            tagMethod(leaf);
        }
    }

    private void tagLeafArgument(DependencyGraphNode leaf) {
        ParameterizedString command = dependencyGraph.getCommand();
        String label = leaf.getLabel();
        Object argument = command.get(label);
        Class<?> clazz = argument.getClass();
        TypeIndividual type = TypeFactory.getInstance().getTypeByReflection(clazz);
        Set<TypeIndividual> typeSingleton = Collections.singleton(type);
        typeMap.put(leaf, typeSingleton);
        //System.out.println("Tagging " + argument + " as: " + typeSingleton);
    }

    private void tagMethod(DependencyGraphNode node) {
        Set<DependencyGraphNode> dependencies = node.getDependencies();
        List<Set<TypeIndividual>> argumentTypes = dependencies.stream()
            .map(typeMap::get)
            .collect(Collectors.toList());

        MethodRanking ranking = rankNode(node, argumentTypes);

        if (ranking.getTopScore() < Settings.THRESHOLD) {
            throw new RankingUnderThresholdException();
        }

        rankingMap.put(node, ranking);

        Set<TypeIndividual> types = ranking.topMethods(Settings.RANKING_SIZE)
                .stream()
                .map(MethodIndividual::getReturnType)
                .collect(Collectors.toSet());

        typeMap.put(node, types);

        //System.out.println(node.getLabel() + " tagged as\n" +  types + "\n" + ranking);

    }

    private MethodRanking rankNode(DependencyGraphNode node, List<Set<TypeIndividual>> types) {
        MethodRanking ranking = RankingCache.getInstance().get(node.getLabel(), types);
        if (ranking != null) {
            //System.out.println("Cached Ranking found!");
            return ranking;
        }

        //System.out.println("Querying CodeOntology...");
        List<MethodIndividual> methods = MethodFactory.getInstance()
                .getMatchingMethods(types, null);

        //System.out.println("Ranking...");
        NaturalLanguageCommand command = new NaturalLanguageCommand(node.getLabel());
        MethodRanker ranker = new MethodRanker(command);
        ranking = ranker.rank(methods);
        RankingCache.getInstance().put(node.getLabel(), types, ranking);
        return ranking;
    }
}
