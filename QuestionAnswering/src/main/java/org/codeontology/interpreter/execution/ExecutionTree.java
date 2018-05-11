package org.codeontology.interpreter.execution;


import org.codeontology.individuals.MethodIndividual;
import org.codeontology.individuals.TypeIndividual;
import org.codeontology.interpreter.dependencies.DependencyGraphNode;
import org.codeontology.interpreter.dependencies.tagged.TaggedDependencyGraph;
import org.codeontology.interpreter.ranking.RankedMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ExecutionTree {

    private TaggedDependencyGraph dependencyGraph;
    private Map<DependencyGraphNode, RankedMethod> methodMap;

    public ExecutionTree(TaggedDependencyGraph dependencyGraph, Map<DependencyGraphNode, RankedMethod> methodMap) {
        if (dependencyGraph == null || methodMap == null) {
            throw new IllegalArgumentException();
        }
        this.dependencyGraph = dependencyGraph;
        this.methodMap = methodMap;
    }

    public double getConfidenceScore() {
        return methodMap.values()
                .stream()
                .mapToDouble(RankedMethod::getScore)
                .average()
                .orElse(0);
    }

    public TypeIndividual getReturnType() {
        DependencyGraphNode root = dependencyGraph.getRoots().get(0);
        MethodIndividual method = methodMap.get(root).getMethod();
        return method.getReturnType();
    }

    public boolean isFeasible() {
        try {
            exec();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Object exec() {
        List<Object> list = dependencyGraph.getRoots().stream()
                .map(this::execSubTree)
                .collect(Collectors.toList());
        int size = list.size();
        return list.get(size - 1);
    }

    private Object execSubTree(DependencyGraphNode root) {
        if (isArgument(root)) {
            String label = root.getLabel();
            return dependencyGraph.getCommand().get(label);
        }
        Set<DependencyGraphNode> nodes = root.getDependencies();
        Object[] args = nodes.stream()
                .map(this::execSubTree)
                .toArray(Object[]::new);
        MethodIndividual method = methodMap.get(root).getMethod();

        try {
            return method.invoke(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        DependencyGraphNode root = dependencyGraph.getRoots().get(0);
        return toString(root);
    }

    private String toString(DependencyGraphNode node) {
        if (isArgument(node)) {
            String label = node.getLabel();
            Object value = dependencyGraph.getCommand().get(label);
            String result = value.toString();
            if (value instanceof String) {
                result = "\"" + result + "\"";
            }
            return result;
        }

        Set<DependencyGraphNode> dependencies = node.getDependencies();

        MethodIndividual method = methodMap.get(node).getMethod();

        if (method.isStatic()) {
            String args = dependencies.stream()
                    .map(this::toString)
                    .collect(Collectors.joining(", "));
            return method.getQualifiedName() + "(" + args + ")";
        }

        List<TypeIndividual> argTypes = dependencies.stream()
                .map(this::typeOf)
                .collect(Collectors.toList());

        int targetIndex = method.guessInstanceIndex(argTypes);
        DependencyGraphNode targetNode = node.getChild(targetIndex);
        List<DependencyGraphNode> depsCopy = new ArrayList<>(dependencies);
        depsCopy.remove(targetIndex);

        String args = depsCopy.stream()
                .map(this::toString)
                .collect(Collectors.joining(", "));

        String target = toString(targetNode);

        return target + "." + method.getName() + "(" + args + ")";
    }

    private TypeIndividual typeOf(DependencyGraphNode node) {
        if (isArgument(node)) {
            Set<TypeIndividual> singleton = dependencyGraph.getTypeTag(node);
            return singleton.toArray(new TypeIndividual[]{})[0];
        }
        return methodMap.get(node)
                .getMethod().getReturnType();
    }

    private boolean isArgument(DependencyGraphNode node) {
        String label = node.getLabel();
        return dependencyGraph.getCommand().hasArgument(label);
    }

}
