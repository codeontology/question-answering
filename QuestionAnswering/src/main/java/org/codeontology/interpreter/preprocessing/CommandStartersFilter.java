package org.codeontology.interpreter.preprocessing;

import java.util.Arrays;
import java.util.List;

public class CommandStartersFilter {

    private static final List<String> commandStarters = Arrays.asList(
            "compute", "raise", "select", "get", "find", "how much is", "what is", "convert", "does", "do", "which is", "how"
    );

    public String filter(String text) {
        text = text.trim();
        String lowerCase = text.toLowerCase();
        for (String starter : commandStarters) {
            if (lowerCase.startsWith(starter)) {
                text = text.replaceFirst("(?i)" + starter, "").trim();
                break;
            }
        }
        return text;
    }
}
