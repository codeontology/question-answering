package org.codeontology.nlp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class StopWordsFilter {

    private Set<String> stopwords = new HashSet<>();

    public StopWordsFilter(String path) throws FileNotFoundException {
        Scanner s = new Scanner(new File(path));
        while (s.hasNextLine()){
            stopwords.add(s.nextLine());
        }
        s.close();
    }

    public List<String> filterStopWords(List<String> sentence) {
        return sentence.stream()
                .filter(token -> !isStopWord(token))
                .collect(Collectors.toList());
    }

    public boolean isStopWord(String word) {
        return word.matches("(\\W)+") || stopwords.contains(word);
    }

}
