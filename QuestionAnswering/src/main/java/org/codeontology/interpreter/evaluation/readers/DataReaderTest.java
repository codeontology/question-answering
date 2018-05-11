package org.codeontology.interpreter.evaluation.readers;

import org.codeontology.Settings;
import org.codeontology.interpreter.evaluation.data.MethodRankingBenchmark;
import org.codeontology.wordvectors.WordVectorsManager;

import java.io.IOException;

public class DataReaderTest {

    public static void main(String[] args) throws IOException {
        WordVectorsManager.load(Settings.lang, Settings.GOOGLE_NEWS_SKIPGRAM_NEGSAMP_EN_300);
        MethodRankingBenchmark data = new MethodRankingBenchmarkReader(Settings.JAVA_METHODS_DATASET_PATH).read();
        data.forEach(System.out::println);
    }
}
