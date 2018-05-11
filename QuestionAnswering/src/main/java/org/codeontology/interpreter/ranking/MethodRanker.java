package org.codeontology.interpreter.ranking;

import info.debatty.java.stringsimilarity.NGram;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import org.apache.commons.collections4.CollectionUtils;
import org.codeontology.individuals.MethodIndividual;
import org.codeontology.interpreter.NaturalLanguageCommand;
import org.codeontology.nlp.NLPProcessor;
import org.codeontology.wordvectors.WordVectorsManager;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MethodRanker implements Comparator<RankedMethod> {

    private NaturalLanguageCommand nlCommand;

    public MethodRanker(NaturalLanguageCommand command) {
        this.nlCommand = command;
    }

    private double nedScore(MethodIndividual method) {
        List<String> annotations = nlCommand.getAnnotations();
        List<String> methodDBpediaLinks = method.getDBpediaLinks();
        Collection<String> intersection = CollectionUtils.intersection(annotations, methodDBpediaLinks);
        if (annotations.size() == 0) {
            return 0;
        }
        return intersection.size() / (double) annotations.size();
    }

    public MethodIndividual bestMatch(List<MethodIndividual> methods) {
        MethodIndividual bestMethod = null;
        double bestScore = -Double.MAX_VALUE;

        for (MethodIndividual method : methods) {
            double currentScore = getScore(method);
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestMethod = method;
            }
        }

        return bestMethod;
    }

    public double getScore(MethodIndividual method) {
        int n = 5;

        double nedScore = nedScore(method);
        if (nlCommand.getAnnotations().size() == 0) {
            n--;
        }

        String description = method.isVoid() ? method.getComment() : method.getReturnDescription();

        double vectorScore = wordEmbeddingsScore(description);
        if (vectorScore == 0) {
            n--;
        }

        //double vectorScoreComment = wordEmbeddingsScore(method.getComment());
        //double vectorScore = Math.max(vectorScoreComment, vectorScoreReturn);

        double editScore = levenshteinSimilarity(method.getLabel());
        double ngoScoreLabel = lemmatizedNGramsOverlaps(1, method.getLabel());
        double labelScore = editScore;
        if (ngoScoreLabel > 0) {
            labelScore = (editScore + ngoScoreLabel) / 2.0;
        }

        //double charNGramsScore = 0; //charNGramsSimilarity(4, method.getLabel());

        double ngoScoreDescription = lemmatizedNGramsOverlaps(1, method.getDescription());
        //double ngoScoreComment = lemmatizedNGramsOverlaps(2, method.getComment());
        //double ngoScore = Math.max(ngoScoreComment, ngoScoreReturn);

        double classNameScore = lemmatizedNGramsOverlaps(1, method.getDeclaringClass().getLabel());

        return (nedScore + labelScore + vectorScore + ngoScoreDescription + classNameScore) / n;
    }

    public RankedMethod getRankedMethod(MethodIndividual method) {
        double score = getScore(method);
        return new RankedMethod(method, score);
    }

    private double wordEmbeddingsScore(String text) {
        if (text == null || text.trim().equals("")) {
            return 0;
        }
        try {
            INDArray methodVector = WordVectorsManager.getVectors().getMeanVector(text);
            return Transforms.cosineSim(nlCommand.getMeanVector(), methodVector);
        } catch (Exception e) {
            return 0;
        }
    }

    private double levenshteinSimilarity(String text) {
        if (text == null) {
            return 0;
        }

        String command = nlCommand.getCommand();
        return new NormalizedLevenshtein().similarity(command, text);
    }

    private double charNGramsSimilarity(int n, String text) {
        if (text == null) {
            return 0;
        }

        String command = nlCommand.getCommand();
        return 1 - new NGram(n).distance(command, text);
    }

    private double lemmatizedNGramsOverlaps(int n, String text) {
        if (text == null) {
            return 0;
        }

        List<String> commandNGrams = nlCommand.getLemmatizedNGrams(n);
        List<String> textNGrams = NLPProcessor.getInstance().getFilteredLemmatizedNGrams(n, text);

        if (textNGrams.size() == 0 || commandNGrams.size() == 0) {
            return 0;
        }

        Collection<String> intersection = CollectionUtils.intersection(commandNGrams, textNGrams);

        return 2.0 * intersection.size() / (commandNGrams.size() + textNGrams.size());
    }

    public MethodRanking rank(List<MethodIndividual> methods) {
        // System.out.println("Ranking size: " + methods.size());
        List<RankedMethod> rankedMethods = methods.stream()
                .map(this::getRankedMethod)
                .collect(Collectors.toList());

        return new MethodRanking(this, rankedMethods);
    }

    @Override
    public int compare(RankedMethod first, RankedMethod second) {
        Double d = first.getScore() - second.getScore();
        return Double.compare(0.0, d);
    }
}