package org.codeontology.interpreter.preprocessing;


public class PreprocessingFilter {
    public String filter(String text) {
        text = new CommandStartersFilter().filter(text);
        if (text.endsWith("?")) {
            text = text.substring(0, text.length() - 1);
        }
        /*text = text.replaceAll("minimum", "min");
        text = text.replaceAll("maximum", "max");
        text = text.replaceAll("size", "length");
        text = text.replaceAll("number of characters", "length");*/

        return new PrepositionConversionFilter().filter(text);
    }
}
