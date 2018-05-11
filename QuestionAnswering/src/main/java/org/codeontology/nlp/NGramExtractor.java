package org.codeontology.nlp;

import opennlp.tools.ngram.NGramGenerator;

import java.util.List;
import java.util.stream.Collectors;

public class NGramExtractor {
    private static final String SEPARATOR = " ";

    public List<String> ngrams(List<String> tokens, int n) {
        return NGramGenerator.generate(tokens, n, SEPARATOR);
    }

    public List<String> flatNGrams(List<List<String>> sentences,  int n) {
        return sentences.stream().flatMap(sentence ->
                ngrams(sentence, n).stream()
        ).collect(Collectors.toList());
    }

}
