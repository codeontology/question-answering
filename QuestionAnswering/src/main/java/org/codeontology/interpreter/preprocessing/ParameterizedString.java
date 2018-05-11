package org.codeontology.interpreter.preprocessing;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.codeontology.interpreter.Literals.*;

public class ParameterizedString {
    private String originalString;
    private String parameterizedString;
    private Map<String, Object> tagMap = new HashMap<>();

    public ParameterizedString(String originalString) {
        this.originalString = originalString;
        tag();
    }

    private void tag() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int covered = 0;
        int end = originalString.length();
        Scanner scanner = new Scanner(originalString);
        String literal = scanner.findInLine(LITERAL_PATTERN);
        while (literal != null) {
            String tag = onLiteralFound(literal, i);
            if (tag != null) {
                int matchStart = scanner.match().start();
                boolean realMatch = true;
                if (matchStart > 0) {
                    char prev = originalString.charAt(matchStart - 1);
                    if (Character.isLetterOrDigit(prev)) {
                        realMatch = false;
                    }
                }

                int matchEnd = scanner.match().end();

                if (matchEnd < originalString.length()) {
                    char next = originalString.charAt(matchEnd);
                    if (Character.isLetterOrDigit(next)) {
                        realMatch = false;
                    }
                }

                if (realMatch) {
                    sb.append(originalString.substring(covered, matchStart))
                            .append(tag);
                    covered = matchEnd;
                    i++;
                }
                literal = scanner.findInLine(LITERAL_PATTERN);
            }
        }
        sb.append(originalString.substring(covered, end));
        parameterizedString = sb.toString();
    }

    private String onLiteralFound(String literal, int index) {
        Object value = null;
        String tag = null;
        if (literal.matches(STRING_PATTERN)) {
            tag = "<string-" + index + ">";
            value = literal.replace("\"", "");
        } else if (literal.matches(DOUBLE_PATTERN)) {
            tag = literal; //"<double-" + index + ">";
            value = Double.parseDouble(literal);
        } else if (literal.matches(INT_PATTERN)) {
            tag = literal; //"<int-" + index + ">";
            value = Integer.parseInt(literal);
        }

        if (tag != null) {
            tagMap.put(tag, value);
        }
        return tag;
    }

    public String getOriginalString() {
        return originalString;
    }

    public String getParameterizedString() {
        return parameterizedString;
    }

    public boolean hasArgument(String label) {
        return get(label) != null;
    }

    @Override
    public String toString() {
        return getParameterizedString();
    }

    public Object get(String tag) {
        return tagMap.get(tag);
    }
}
