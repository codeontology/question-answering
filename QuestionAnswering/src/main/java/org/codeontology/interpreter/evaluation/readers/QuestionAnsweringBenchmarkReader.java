package org.codeontology.interpreter.evaluation.readers;

import org.codeontology.Settings;
import org.codeontology.interpreter.evaluation.data.QuestionAnsweringBenchmark;
import org.codeontology.interpreter.evaluation.data.QuestionAnsweringInstance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.codeontology.interpreter.Literals.*;


public class QuestionAnsweringBenchmarkReader implements DatasetReader<QuestionAnsweringInstance> {
    private final String path;

    public QuestionAnsweringBenchmarkReader(String path) {
        this.path = path;
    }

    @Override
    public QuestionAnsweringBenchmark read() throws IOException {
        return Files.lines(Paths.get(path))
                .map(this::parseInstance)
                .filter(Objects::nonNull)
                .collect(QuestionAnsweringBenchmark.collector());
    }

    private QuestionAnsweringInstance parseInstance(String line) {
        String[] items = line.split("\\t+");

        if (items.length < 2) {
            return null;
        }

        String question = items[0];
        String literal = items[1];
        Object answer = null;

        if (literal.matches(STRING_PATTERN)) {
            answer = literal.replace("\"", "");
        } else if (literal.matches(DOUBLE_PATTERN)) {
            answer = Double.parseDouble(literal);
        } else if (literal.matches(INT_PATTERN)) {
            answer = Double.parseDouble(literal);
        } else if (literal.matches(BOOLEAN_PATTERN)) {
            answer = Boolean.parseBoolean(literal);
        }

        if (answer == null) {
            throw new RuntimeException("Cannot read literal: " + literal);
        }

        return new QuestionAnsweringInstance(question, answer);
    }

    public static void main(String[] args) throws IOException {
        new QuestionAnsweringBenchmarkReader(Settings.QUESTION_ANSWERING_BENCHMARK)
                .read().forEach(System.out::println);
    }
}
