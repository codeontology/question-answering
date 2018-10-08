package org.codeontology.interpreter.preprocessing;


public class PreprocessingFilter {
    public String filter(String text) {
        text = new CommandStartersFilter().filter(text);
        
        if (text.endsWith("?")) {
            text = text.substring(0, text.length() - 1);
        }

        return new WordConversionFilter().filter(text);
    }
}
