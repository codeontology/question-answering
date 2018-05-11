package org.codeontology.interpreter;


public class Literals {
    public static final String STRING_PATTERN = "(\".*?\")";
    public static final String INT_PATTERN = "((-|\\+)?[0-9]+)";
    public static final String DOUBLE_PATTERN = "((-|\\+)?[0-9]+\\.[0-9]+)";
    public static final String BOOLEAN_PATTERN = "(true|false)";
    public static final String LITERAL_PATTERN = STRING_PATTERN + "|" + DOUBLE_PATTERN + "|" + INT_PATTERN;
}
