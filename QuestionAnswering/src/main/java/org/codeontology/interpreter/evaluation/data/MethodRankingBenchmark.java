package org.codeontology.interpreter.evaluation.data;

import org.codeontology.individuals.MethodIndividual;
import org.codeontology.interpreter.NaturalLanguageCommand;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MethodRankingBenchmark extends Dataset<LabeledNLCommand> {

    public MethodRankingBenchmark() {
    }

    public MethodRankingBenchmark(int initialCapacity) {
        super(initialCapacity);
    }

    public MethodRankingBenchmark(Collection<? extends LabeledNLCommand> c) {
        super(c);
    }

    public MethodRankingBenchmark(Dataset<? extends LabeledNLCommand> other) {
        super(other);
    }

    public List<List<MethodIndividual>> getMethods() {
        return stream().map(LabeledNLCommand::getMethods)
                .collect(Collectors.toList());
    }

    public List<NaturalLanguageCommand> getCommands() {
        return stream().map(LabeledNLCommand::getCommand)
                .collect(Collectors.toList());
    }

    public static Collector<LabeledNLCommand, ?, MethodRankingBenchmark> collector() {
        return Collector.of(
                MethodRankingBenchmark::new,
                MethodRankingBenchmark::add,
                (set1, set2) -> {
                    set1.addAll(set2);
                    return set1;
                },
                Collector.Characteristics.IDENTITY_FINISH
        );
    }
}
