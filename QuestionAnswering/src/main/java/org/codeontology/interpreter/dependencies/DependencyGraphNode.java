package org.codeontology.interpreter.dependencies;

import edu.stanford.nlp.ling.IndexedWord;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.*;

public class DependencyGraphNode implements Comparable<DependencyGraphNode> {
    private String label;
    private Set<DependencyGraphNode> dependencies;
    private IndexedWord indexedWord;

    public DependencyGraphNode(IndexedWord word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        this.indexedWord = word;
        this.label = word.originalText();
        this.dependencies = new TreeSet<>();
    }

    public void addChild(DependencyGraphNode node) {
        dependencies.add(node);
    }

    public void addAllChildren(Collection<? extends DependencyGraphNode> nodes) {
        dependencies.addAll(nodes);
    }

    public void removeChild(DependencyGraphNode node) {
        dependencies.remove(node);
    }

    public void removeAllChildren(Collection<? extends DependencyGraphNode> nodes) {
        dependencies.removeAll(nodes);
    }

    public void removeAllChildren() {
        dependencies = new TreeSet<>();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Set<DependencyGraphNode> getDependencies() {
        return dependencies;
    }

    public DependencyGraphNode getChild(int i) {
        return new ArrayList<>(dependencies).get(i);
    }

    public int outDegree() {
        return dependencies.size();
    }

    public boolean isLeaf() {
        return outDegree() == 0;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DependencyGraphNode)) return false;

        DependencyGraphNode that = (DependencyGraphNode) o;

        return indexedWord.equals(that.indexedWord);
    }

    @Override
    public int hashCode() {
        return indexedWord.hashCode();
    }

    @Override
    public int compareTo(@NonNull DependencyGraphNode other) {
        return this.indexedWord.index() - other.indexedWord.index();
    }
}
