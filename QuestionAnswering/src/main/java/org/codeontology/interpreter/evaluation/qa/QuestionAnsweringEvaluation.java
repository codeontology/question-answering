package org.codeontology.interpreter.evaluation.qa;

import org.codeontology.Settings;
import org.codeontology.Utils;
import org.codeontology.interpreter.evaluation.data.QuestionAnsweringBenchmark;
import org.codeontology.interpreter.evaluation.readers.QuestionAnsweringBenchmarkReader;

import java.io.IOException;

public class QuestionAnsweringEvaluation {
    public static void main(String[] args) throws IOException {
        Utils.configureLog();
        //System.out.println("Loading Word Vectors...");
        //WordVectorsManager.load(Settings.lang, Settings.GOOGLE_NEWS_SKIPGRAM_NEGSAMP_EN_300);
        //System.out.println("Word Vectors Loaded Successfully.");
        //System.out.println();

        QuestionAnsweringBenchmark dataset = new QuestionAnsweringBenchmarkReader(Settings.QUESTION_ANSWERING_BENCHMARK).read();

        long tic = System.currentTimeMillis();
        QuestionAnsweringEvaluator evaluator = new QuestionAnsweringEvaluator(dataset);
        long toc = System.currentTimeMillis();

        double timeSec = (toc - tic) / 1000.0;

        double accuracy = evaluator.accuracy();
        System.out.println();
        System.out.println("Time: " + timeSec);
        System.out.println("Time per question: " + (timeSec / dataset.size()));
        System.out.println("Accuracy: " + accuracy);
        int correctAnswers = (int) Math.round(accuracy * dataset.size());
        System.out.println(correctAnswers + " out of " + dataset.size());
    }
}
