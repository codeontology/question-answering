package org.codeontology.nlp;

import edu.stanford.nlp.simple.Sentence;

import java.util.List;

public class Lemmatizer {

    public Lemmatizer() {

    }

    public List<String> lemmatize(List<String> tokens) {
        return new Sentence(tokens).lemmas();
    }

}
