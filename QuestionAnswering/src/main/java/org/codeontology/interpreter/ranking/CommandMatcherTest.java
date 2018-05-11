package org.codeontology.interpreter.ranking;


import org.codeontology.Settings;
import org.codeontology.individuals.MethodIndividual;
import org.codeontology.interpreter.NaturalLanguageCommand;
import org.codeontology.interpreter.evaluation.ranking.MethodRankingEvaluator;
import org.codeontology.interpreter.evaluation.data.LabeledNLCommand;
import org.codeontology.interpreter.evaluation.data.MethodRankingBenchmark;
import org.codeontology.wordvectors.WordVectorsManager;

import java.util.Collections;

import static org.codeontology.query.Prefixes.WOC;

public class CommandMatcherTest {
    public static void main(String[] args) throws Exception {
        WordVectorsManager.load(Settings.lang, Settings.GOOGLE_NEWS_SKIPGRAM_NEGSAMP_EN_300);
        String cmdText = "Convert to lower case";
        NaturalLanguageCommand nlCommand = new NaturalLanguageCommand(cmdText);
        MethodIndividual goldMethod = new MethodIndividual(WOC + "java.lang.String-toLowerCase()");
        LabeledNLCommand match = new LabeledNLCommand(nlCommand, Collections.singletonList(goldMethod));

        MethodRankingBenchmark data = new MethodRankingBenchmark(Collections.singletonList(match));
        MethodRankingEvaluator evaluator = new MethodRankingEvaluator(data);
        System.out.println(evaluator.accuracy());
        System.out.println(evaluator.meanAveragePrecision());
    }
}
