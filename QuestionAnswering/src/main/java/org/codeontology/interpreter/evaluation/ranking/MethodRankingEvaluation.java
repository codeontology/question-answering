package org.codeontology.interpreter.evaluation.ranking;

import org.codeontology.Settings;
import org.codeontology.Utils;
import org.codeontology.interpreter.evaluation.data.MethodRankingBenchmark;
import org.codeontology.interpreter.evaluation.readers.MethodRankingBenchmarkReader;
import org.codeontology.wordvectors.WordVectorsManager;

import java.io.IOException;

public class MethodRankingEvaluation {
    public static void main(String[] args) throws IOException {
        Utils.configureLog();
        System.out.println("Loading Word Vectors...");
        WordVectorsManager.load(Settings.lang, Settings.GOOGLE_NEWS_SKIPGRAM_NEGSAMP_EN_300);
        System.out.println("Word Vectors Loaded Successfully.");
        System.out.println();
        long tic = System.currentTimeMillis();
        MethodRankingBenchmark dataset = new MethodRankingBenchmarkReader(Settings.JAVA_METHODS_DATASET_PATH).read();

        MethodRankingEvaluator evaluator = new MethodRankingEvaluator(dataset);

        double accuracy = evaluator.accuracy();
        System.out.println("MAP@1: " + accuracy);

        double map = evaluator.meanAveragePrecision();
        System.out.println("MAP: " + map);
        long toc = System.currentTimeMillis();

        double timeSec = (toc - tic) / 1000.0;
        System.out.println("Time: " + timeSec);
    }
}
