package org.codeontology.nlp;

import org.codeontology.Settings;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

public class NLPProcessor {

    private SentenceDetector sentenceDetector;
    private Tokenizer tokenizer;
    private Lemmatizer lemmatizer;
    private NGramExtractor nGramExtractor;
    private StopWordsFilter stopWordsFilter;

    private static NLPProcessor instance = new NLPProcessor();

    private NLPProcessor() {
        try {
            sentenceDetector = new SentenceDetector(Settings.EN_SENTENCE_DETECTOR);
            tokenizer = new Tokenizer(Settings.EN_TOKENIZER);
            lemmatizer = new Lemmatizer();
            stopWordsFilter = new StopWordsFilter(Settings.EN_STOPWORDS);
            nGramExtractor = new NGramExtractor();
        } catch (NLPModelLoadingException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static NLPProcessor getInstance() {
        return instance;
    }

    public List<String> getFilteredLemmatizedNGrams(int n, String text) {
        List<String> sentences = sentenceDetector.detectSentences(text);

        List<List<String>> tokens = tokenizer.tokenizeAll(sentences);

        List<List<String>> lemmas = tokens.stream()
                .map(lemmatizer::lemmatize)
                .collect(Collectors.toList());

        List<List<String>> filteredLemmas = lemmas.stream()
                .map(stopWordsFilter::filterStopWords)
                .collect(Collectors.toList());

        return nGramExtractor.flatNGrams(filteredLemmas, n);

    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }
}