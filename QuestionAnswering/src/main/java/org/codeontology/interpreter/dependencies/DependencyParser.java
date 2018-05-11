package org.codeontology.interpreter.dependencies;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.simple.Sentence;
import org.codeontology.interpreter.preprocessing.ParameterizedString;

import java.util.*;
import java.util.stream.Collectors;

public class DependencyParser {

    private ParameterizedString command;
    private SemanticGraph semanticGraph;

    public DependencyParser(ParameterizedString command) {
        this.command = command;
        Sentence sentence = new Sentence(this.command.toString());
        this.semanticGraph = sentence.dependencyGraph();
        //System.out.println(semanticGraph);
    }

    public List<DependencyGraphNode> parse() {
        List<DependencyGraphNode> roots = semanticGraph.getRoots().stream()
                .flatMap(root -> parseRoot(root).stream())
                .collect(Collectors.toList());

        return roots.stream().map(this::postProcessing)
                .collect(Collectors.toList());
    }

    private Set<DependencyGraphNode> parseRoot(IndexedWord root) {
        DependencyGraphNode node = parseSubTree(root);
        if (isNegligibleTag(root.tag())) {
            return node.getDependencies();
        }
        return Collections.singleton(node);
    }

    private DependencyGraphNode parseSubTree(IndexedWord root) {
        List<SemanticGraphEdge> edges = semanticGraph.getOutEdgesSorted(root);
        DependencyGraphNode node = new DependencyGraphNode(root);
        edges.forEach(e -> parseEdge(node, e));
        return node;
    }

    private void parseEdge(DependencyGraphNode node, SemanticGraphEdge edge) {
        IndexedWord target = edge.getTarget();
        String relation = edge.getRelation().toString();
        DependencyGraphNode child = parseSubTree(target);

        if (child.isLeaf() && isNegligibleRelation(relation)) {
            return;
        }

        if (isBindingRelation(relation) &&
                !command.hasArgument(node.getLabel()) &&
                !command.hasArgument(child.getLabel())) {
            String label = child.getLabel() + " " + node.getLabel();
            node.setLabel(label);
            child.getDependencies().forEach(node::addChild);
            return;
        }

        if (isNegligibleTag(target.tag())) {
            child.getDependencies().forEach(node::addChild);
            return;
        }

        node.addChild(child);
    }

    private DependencyGraphNode postProcessing(DependencyGraphNode root) {
        removeRedundantSubTrees(root);
        return pushArguments(root);
    }

    private void removeRedundantSubTrees(DependencyGraphNode root) {
        if (root == null) {
            return;
        }

        Queue<DependencyGraphNode> queue = new LinkedList<>();
        Set<DependencyGraphNode> visited = new HashSet<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            DependencyGraphNode node = queue.remove();
            Set<DependencyGraphNode> dependencies = node.getDependencies();
            dependencies.removeAll(visited);
            visited.addAll(dependencies);
            queue.addAll(dependencies);
        }
    }

    private DependencyGraphNode pushArguments(DependencyGraphNode root) {
        List<DependencyGraphNode> dependencies = new ArrayList<>(root.getDependencies());

        boolean rootArg = false;
        if (command.hasArgument(root.getLabel()) && !root.isLeaf()) {
            dependencies = Collections.singletonList(root);
            rootArg = true;
        }

        DependencyGraphNode parent = root;

        for (DependencyGraphNode node : dependencies) {
            if (command.hasArgument(node.getLabel())) {
                List<DependencyGraphNode> nodeDeps = new ArrayList<>(node.getDependencies());
                List<DependencyGraphNode> args = nodeDeps.stream()
                        .filter(n -> command.hasArgument(n.getLabel()))
                        .collect(Collectors.toList());
                nodeDeps.removeAll(args);
                node.removeAllChildren();
                if (!nodeDeps.isEmpty()) {
                    root.removeChild(node);
                    parent = nodeDeps.get(0);
                    parent.addChild(node);
                    if (!rootArg) {
                        nodeDeps.remove(parent);
                        parent.addAllChildren(nodeDeps);
                    } else {
                        root.addAllChildren(nodeDeps);
                    }
                }
                parent.addAllChildren(args);
            }
        }

        root.getDependencies().forEach(this::pushArguments);
        return parent;

    }

    private boolean isNegligibleRelation(String relation) {
        List<String> ignoredRelations = Arrays.asList("case", "cc", "conj", "det", "punct", "cop", "aux");
        return ignoredRelations.stream().filter(relation::startsWith).count() > 0;
    }

    private boolean isBindingRelation(String relation) {
        List<String> bindingRelations = Arrays.asList("amod", "compound", "goeswith", "mark", "flat", "fixed", "advmod");
        return bindingRelations.stream().filter(relation::startsWith).count() > 0;
    }

    private boolean isNegligibleTag(String tag) {
        List<String> negligibleTags = Arrays.asList("IN", "DT", "CC", "WP", "TO");
        return negligibleTags.contains(tag);
    }
}

