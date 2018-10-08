package org.codeontology;

public class Settings {
    public static final String MAIN_ENDPOINT = "http://localhost:3030/OpenJDK/query";
    public static final String LANG_ENDPOINT = "http://localhost:3030/OpenJDK_Lang/query";
    public static final String ENDPOINT = MAIN_ENDPOINT;

    private static final String NLP = "../nlp/";

    public static final String lang = "en";
    public static final String EN_SENTENCE_DETECTOR = NLP + "en-sent.bin";
    public static final String EN_TOKENIZER = NLP + "en-token.bin";

    public static final String EN_STOPWORDS = "../nlp/en-stopwords.txt";

    private static final String WORD_VECTORS = "../nlp/WordVectors/";

    public static final String GOOGLE_NEWS_SKIPGRAM_NEGSAMP_EN_300 = WORD_VECTORS + "GoogleNews-vectors-negative300.bin.gz";
    public static final String GLOVE_300 = WORD_VECTORS + "glove.6B.300d.txt";
    public static final String OpenJDKVectors = WORD_VECTORS + "OpenJDKVectors.txt";

    public static final String JAVA_METHODS_DATASET_PATH = "../JavaMethods.tsv";
    public static final String QUESTION_ANSWERING_BENCHMARK = "../QuestionAnsweringBenchmark.tsv";

    public static final int RANKING_SIZE = 15;
    public static final double THRESHOLD = 0.015;
}
