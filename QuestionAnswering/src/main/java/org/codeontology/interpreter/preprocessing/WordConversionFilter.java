package org.codeontology.interpreter.preprocessing;

import java.util.HashMap;
import java.util.Map;

public class WordConversionFilter {

    private static final Map<String, String> wordsMap = new HashMap<>();

    public WordConversionFilter() {
        wordsMap.put("between", "of");
        wordsMap.put("maximum", "max");
        wordsMap.put("minimum", "min");
        wordsMap.put("size", "length");
        wordsMap.put("long", "length");
        wordsMap.put("number of characters", "length");
    }

    public String filter(String text) {
        text = text.trim();
        for (String word : wordsMap.keySet()) {
            text = text.replace(word, wordsMap.get(word));
        }
        return text;
    }

}