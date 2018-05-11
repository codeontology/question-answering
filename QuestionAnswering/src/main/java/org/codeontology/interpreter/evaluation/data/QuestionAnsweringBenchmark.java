package org.codeontology.interpreter.evaluation.data;


import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class QuestionAnsweringBenchmark extends Dataset<QuestionAnsweringInstance> {

    public QuestionAnsweringBenchmark() {
    }

    public QuestionAnsweringBenchmark(int initialCapacity) {
        super(initialCapacity);
    }

    public QuestionAnsweringBenchmark(Collection<? extends QuestionAnsweringInstance> c) {
        super(c);
    }

    public QuestionAnsweringBenchmark(Dataset<? extends QuestionAnsweringInstance> other) {
        super(other);
    }

    public List<String> getQuestions() {
        return stream().map(QuestionAnsweringInstance::getQuestion)
                .collect(Collectors.toList());
    }

    public List<Object> getAnswers() {
        return stream().map(QuestionAnsweringInstance::getAnswer)
                .collect(Collectors.toList());
    }

    public static Collector<QuestionAnsweringInstance, ?, QuestionAnsweringBenchmark> collector() {
        return Collector.of(
                QuestionAnsweringBenchmark::new,
                QuestionAnsweringBenchmark::add,
                (set1, set2) -> {
                    set1.addAll(set2);
                    return set1;
                },
                Collector.Characteristics.IDENTITY_FINISH
        );
    }

}
