package org.codeontology.interpreter;

import org.codeontology.Settings;
import org.codeontology.Utils;
import org.codeontology.wordvectors.WordVectorsManager;

import java.util.Arrays;

public class SemanticInterpreterTest {
    public static void main(String[] args) throws Exception {
        Utils.configureLog();

        SemanticInterpreter interpreter = SemanticInterpreter.getInstance();

        System.out.println("Loading Word Vectors...");
        WordVectorsManager.load(Settings.lang, Settings.GOOGLE_NEWS_SKIPGRAM_NEGSAMP_EN_300);
        System.out.println("Word vectors loaded successfully");
        System.out.println();

        double d = interpreter.invokeStaticMethod("compute the cube root", double.class, 27.0);
        System.out.println(d);

        String command = "Sum 1 to the length of \"abcd\"";
        System.out.println(command);
        System.out.println();
        Object result = interpreter.exec(command);
        System.out.println();
        System.out.println("Result: " + result);
        System.out.println();

        command = "Convert \"abcd\" to upper case";
        System.out.println(command);
        System.out.println();
        result = interpreter.exec(command);
        System.out.println();
        System.out.println("Result: " + result);
        System.out.println();

        int[] array = new int[10];
        interpreter.invokeStaticMethod("fill array", void.class, array, 72);
        Arrays.stream(array).forEach(System.out::println);

        System.out.println();

        d = interpreter.invokeStaticMethod("compute the cube root", double.class, 27);
        System.out.println(d);

        double i = interpreter.invokeStaticMethod("sum", double.class, 27, 20);
        System.out.println(i);

        System.out.println();
        String substringTest = "substringTest";
        String substring = interpreter.invokeInstanceMethod("substring", String.class, substringTest, 0, 9);
        System.out.println(substring);

        System.out.println();
        String upper = interpreter.invokeInstanceMethod("convert to upper case", String.class, "This Message Should Be UpperCase");
        System.out.println(upper);
    }
}
