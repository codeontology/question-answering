package org.codeontology.interpreter.evaluation.qa;

import com.google.common.collect.Streams;
import org.codeontology.interpreter.SemanticInterpreter;
import org.codeontology.interpreter.evaluation.data.QuestionAnsweringBenchmark;
import org.codeontology.interpreter.evaluation.data.QuestionAnsweringInstance;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuestionAnsweringEvaluator {

    private QuestionAnsweringBenchmark benchmark;
    private SemanticInterpreter interpreter;
    private List<Object> answers;

    public QuestionAnsweringEvaluator(QuestionAnsweringBenchmark benchmark) {
        this.benchmark = benchmark;
        this.interpreter = SemanticInterpreter.getInstance();
        answers = computeAnswers();
    }

    private List<Object> computeAnswers() {
        return benchmark.stream().map(this::answer)
                .collect(Collectors.toList());
    }

    public double accuracy() {
        Stream<Object> answerStream = answers.stream();
        Stream<Object> goldStream = benchmark.getAnswers().stream();

        long correctPredictions = Streams.zip(
                answerStream,
                goldStream,
                this::matches
        ).filter(x -> x).count();

        return correctPredictions / (double) benchmark.size();
    }


    private Object answer(QuestionAnsweringInstance instance) {
        System.out.println("Running on " + instance);
        String question = instance.getQuestion();
        Object gold = instance.getAnswer();
        Object result = null;


        Class<?> returnType = null;
        if (gold != null) {
            returnType = gold.getClass();
        }

        try {
            result = interpreter.exec(question, returnType);
        } catch(Exception e) {
            System.out.println("Unanswered");
            return null;
        }

        //System.out.println();
        //System.out.println("Gold:\t" + gold);
        //System.out.println("Result:\t" + result);
        System.out.println("PASSED: " + matches(result, gold));
        //System.out.println();

        return result;
    }

    private boolean matches(Object result, Object gold) {
        if (result == null && gold == null) {
            return true;
        }
        if (result == null) {
            return false;
        }
        if (result.equals(gold)) {
            return true;
        }
        if (result instanceof Double && gold instanceof Double) {
            double threshold = 0.1;
            double value = (Double) result;
            double goldValue = (Double) gold;
            return Math.abs(value - goldValue) <= threshold;
        }
        return false;
    }
}
