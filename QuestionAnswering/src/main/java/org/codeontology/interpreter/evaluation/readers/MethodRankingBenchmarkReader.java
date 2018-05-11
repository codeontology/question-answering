package org.codeontology.interpreter.evaluation.readers;

import org.codeontology.individuals.MethodIndividual;
import org.codeontology.interpreter.NaturalLanguageCommand;
import org.codeontology.interpreter.evaluation.data.LabeledNLCommand;
import org.codeontology.interpreter.evaluation.data.MethodRankingBenchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodRankingBenchmarkReader implements DatasetReader<LabeledNLCommand> {

    private final String path;

    public MethodRankingBenchmarkReader(String path) {
        this.path = path;
    }

    @Override
    public MethodRankingBenchmark read() throws IOException {
        return Files.lines(Paths.get(path))
                .map(this::parseNLCommandMatch)
                .collect(MethodRankingBenchmark.collector());
    }

    private LabeledNLCommand parseNLCommandMatch(String line) {
        String[] items = line.split("\\t+");
        NaturalLanguageCommand command = new NaturalLanguageCommand(items[0]);
        String[] uris = Arrays.copyOfRange(items, 1, items.length);

        List<MethodIndividual> methods = Arrays.stream(uris)
                .map(MethodIndividual::new)
                .collect(Collectors.toList());

        return new LabeledNLCommand(command, methods);
    }
}
