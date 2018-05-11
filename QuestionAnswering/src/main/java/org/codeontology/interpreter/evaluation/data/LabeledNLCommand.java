package org.codeontology.interpreter.evaluation.data;


import org.codeontology.individuals.MethodIndividual;
import org.codeontology.interpreter.NaturalLanguageCommand;

import java.util.List;

public class LabeledNLCommand {
    private NaturalLanguageCommand command;
    private List<MethodIndividual> methods;

    public LabeledNLCommand(NaturalLanguageCommand command, List<MethodIndividual> methods) {
        this.command = command;
        this.methods = methods;
    }

    public NaturalLanguageCommand getCommand() {
        return command;
    }

    public List<MethodIndividual> getMethods() {
        return methods;
    }

    @Override
    public String toString() {
        return "(" + getCommand() + ",\t" + getMethods() + ")";
    }
}
