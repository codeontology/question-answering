package org.codeontology.interpreter.evaluation.ranking;

import com.google.common.collect.Streams;
import org.apache.lucene.benchmark.quality.QualityStats;
import org.codeontology.individuals.MethodIndividual;
import org.codeontology.individuals.TypeIndividual;
import org.codeontology.interpreter.ranking.MethodRanking;
import org.codeontology.interpreter.NaturalLanguageCommand;
import org.codeontology.interpreter.SemanticInterpreter;
import org.codeontology.interpreter.evaluation.data.LabeledNLCommand;
import org.codeontology.interpreter.evaluation.data.MethodRankingBenchmark;

import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodRankingEvaluator {

    private MethodRankingBenchmark dataset;
    private SemanticInterpreter interpreter;
    private List<MethodRanking> predictions;

    public MethodRankingEvaluator(MethodRankingBenchmark dataset) {
        this.dataset = dataset;
        this.interpreter = SemanticInterpreter.getInstance();
        predictions = computePredictions();
    }

    private List<MethodRanking> computePredictions() {
        return dataset.stream().map(this::rankOn)
                .collect(Collectors.toList());
    }

    public double accuracy() {
        Stream<MethodRanking> rankingStream = predictions.stream();
        Stream<List<MethodIndividual>> methodStream = dataset.getMethods().stream();

        long correctPredictions = Streams.zip(
                rankingStream,
                methodStream,
                (ranking, methods) -> methods.contains(ranking.top().getMethod())
        ).filter(x -> x).count();

        return correctPredictions / (double) dataset.size();
    }

    private double averagePrecision(MethodRanking ranking, List<MethodIndividual> methods) {
        long time = System.currentTimeMillis();
        QualityStats stats = new QualityStats(methods.size(), time);
        int n = 1;

        for (MethodIndividual result : ranking.getMethods()) {
            boolean relevant = methods.contains(result);
            stats.addResult(n, relevant, time);
            n++;
        }

        return stats.getAvp();
    }

    public double meanAveragePrecision() {
        Stream<MethodRanking> rankings = predictions.stream();
        Stream<List<MethodIndividual>> methods = dataset.getMethods().stream();

        OptionalDouble d = Streams.zip(rankings, methods, this::averagePrecision)
                .mapToDouble(x -> x)
                .average();

        if (!d.isPresent()) {
            return 0;
        }

        return d.getAsDouble();
    }

    private MethodRanking rankOn(LabeledNLCommand match) {
        System.out.println("Running on " + match);
        NaturalLanguageCommand command = match.getCommand();
        List<MethodIndividual> methods = match.getMethods();
        MethodIndividual goldMethod = methods.get(0);
        TypeIndividual returnType = goldMethod.getReturnType();
        List<TypeIndividual> types = goldMethod.getFormalTypeParameters();

        if (!goldMethod.isStatic()) {
            TypeIndividual declaringClass = goldMethod.getDeclaringClass();
            types.add(declaringClass);
        }

        MethodRanking result = interpreter.rankMethods(command, returnType, types);
        System.out.println(result);
        System.out.println();
        System.out.println(methods.contains(result.topMethod()));
        System.out.println(averagePrecision(result, methods));
        System.out.println();
        System.out.println();
        return result;
    }
}
