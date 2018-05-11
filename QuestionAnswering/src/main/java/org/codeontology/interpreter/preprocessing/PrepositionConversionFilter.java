package org.codeontology.interpreter.preprocessing;

import java.util.Collections;
import java.util.List;

public class PrepositionConversionFilter {

    private static final List<String> prepositions = Collections.singletonList("between");

    public String filter(String text) {
        text = text.trim();
        for (String preposition : prepositions) {
            text = text.replace(preposition, "of");
        }
        return text;
    }

}
