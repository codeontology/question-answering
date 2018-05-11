package org.codeontology.interpreter;

import org.codeontology.ned.EntityLinker;
import org.codeontology.nlp.NLPProcessor;
import org.codeontology.wordvectors.WordVectorsManager;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.List;

public class NaturalLanguageCommand {

    private final String command;
    private List<String> annotations;
    private INDArray meanVector;
    private List<String> lemmatizedUnigrams;
    private List<String> lemmatizedBigrams;

    public NaturalLanguageCommand(String command) {
        if (command == null) {
            throw new IllegalArgumentException("command cannot be null");
        }
        this.command = command;
        annotations = new EntityLinker().linkEntities(command);
        lemmatizedUnigrams = NLPProcessor.getInstance().getFilteredLemmatizedNGrams(1, command);
        lemmatizedBigrams = NLPProcessor.getInstance().getFilteredLemmatizedNGrams(2, command);
        try {
            meanVector = WordVectorsManager.getVectors().getMeanVector(command);
        } catch (Exception e) {
            meanVector = null;
        }
    }

    public String getCommand() {
        return command;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public INDArray getMeanVector() {
        return meanVector;
    }

    public List<String> getLemmatizedNGrams(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be greater than 0");
        }

        switch (n) {
            case 1: return lemmatizedUnigrams;
            case 2: return lemmatizedBigrams;
            default: return NLPProcessor.getInstance().getFilteredLemmatizedNGrams(n, command);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NaturalLanguageCommand)) return false;

        NaturalLanguageCommand that = (NaturalLanguageCommand) o;

        return command.equals(that.command);
    }

    @Override
    public int hashCode() {
        return command.hashCode();
    }

    @Override
    public String toString() {
        return command;
    }
}
