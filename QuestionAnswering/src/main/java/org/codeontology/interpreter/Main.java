package org.codeontology.interpreter;

import org.codeontology.Settings;
import org.codeontology.Utils;
import org.codeontology.wordvectors.WordVectorsManager;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public  static void main(String[] args) throws Exception {
        Utils.configureLog();

        System.out.println("Loading Word Vectors...");
        WordVectorsManager.load(Settings.lang, Settings.GOOGLE_NEWS_SKIPGRAM_NEGSAMP_EN_300);
        System.out.println("Word vectors loaded successfully");
        System.out.println();


        SemanticInterpreter interpreter = SemanticInterpreter.getInstance();
        clearScreen();
        Scanner s = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String command = s.nextLine();
            Object result = interpreter.exec(command);
            System.out.println();
            System.out.println("Answer: " + result);
            System.out.println();
        }
    }

    private static void clearScreen() {
        for (int i = 0; i < 500; i++) {
            System.out.println();
        }
        try {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (IOException | InterruptedException e) {
            //
        }
    }
}
