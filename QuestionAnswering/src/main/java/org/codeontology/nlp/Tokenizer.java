package org.codeontology.nlp;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.codeontology.Settings;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Tokenizer {

    private TokenizerME tokenizer;

    public Tokenizer(String path) throws NLPModelLoadingException {
        try {
            InputStream inputStream = new FileInputStream(path);
            TokenizerModel tokenModel = new TokenizerModel(inputStream);
            tokenizer = new TokenizerME(tokenModel);
        } catch (IOException e) {
            throw new NLPModelLoadingException(path);
        }
    }

    public List<String> tokenize(String text) {
        if (text == null || text.trim().equals("")) {
            return new ArrayList<>();
        }

        String[] tokens = tokenizer.tokenize(text);
        return Arrays.asList(tokens);
    }

    public List<List<String>> tokenizeAll(List<String> tokens) {
        return tokens.stream().map(this::tokenize).collect(Collectors.toList());
    }

    public static void main(String[] args) throws NLPModelLoadingException {
        Tokenizer t = new Tokenizer(Settings.EN_TOKENIZER);
        t.tokenize("Compute the cube root of 12.3. Then compute the max of 32 and 12.3.").forEach(System.out::println);
    }

}
