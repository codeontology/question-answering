package org.codeontology.interpreter.dependencies;

import org.codeontology.interpreter.preprocessing.ParameterizedString;
import org.codeontology.interpreter.preprocessing.PreprocessingFilter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DependencyGraph {
    private List<DependencyGraphNode> roots;
    private ParameterizedString command;

    public DependencyGraph(String command) {
        if (command == null) {
            throw new IllegalArgumentException();
        }
        String filteredCommand = new PreprocessingFilter().filter(command);
        this.command = new ParameterizedString(filteredCommand);
        //System.out.println(this.command);
        this.roots = new DependencyParser(this.command).parse();
    }

    @Override
    public String toString() {
        return roots.stream()
                .map(root -> toString(root, 0))
                .collect(Collectors.joining("\n"));
    }

    public String toString(DependencyGraphNode root, int level) {
        String indentation = IntStream.range(0, level)
                .mapToObj(i -> "  ")
                .collect(Collectors.joining(""));

        String deps = root.getDependencies().stream()
                .map(dep -> toString(dep, level + 1))
                .collect(Collectors.joining("\n"));

        if (!deps.isEmpty()) {
            deps = "\n" + deps;
        }

        return indentation + root + deps;

    }

    public List<DependencyGraphNode> getRoots() {
        return roots;
    }

    public ParameterizedString getCommand() {
        return command;
    }
}
