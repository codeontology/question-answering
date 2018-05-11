package org.codeontology.interpreter;

import com.google.common.primitives.Primitives;
import org.codeontology.individuals.MethodIndividual;
import org.codeontology.individuals.TypeIndividual;
import org.codeontology.interpreter.dependencies.DependencyGraph;
import org.codeontology.interpreter.dependencies.tagged.NoFeasibleTreeException;
import org.codeontology.interpreter.dependencies.tagged.TaggedDependencyGraph;
import org.codeontology.interpreter.execution.ExecutionTree;
import org.codeontology.interpreter.ranking.MethodRanker;
import org.codeontology.interpreter.ranking.MethodRanking;
import org.codeontology.query.MethodFactory;
import org.codeontology.query.TypeFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SemanticInterpreter {
    private static SemanticInterpreter interpreter = new SemanticInterpreter();
    private TypeFactory typeFactory = TypeFactory.getInstance();
    private MethodFactory methodFactory = MethodFactory.getInstance();

    public static SemanticInterpreter getInstance() {
        return interpreter;
    }

    private SemanticInterpreter() {
    }

    public <T> T invokeStaticMethod(String command, Class<T> returnClass, Object... parameters) throws Exception {
        NaturalLanguageCommand nlCommand = new NaturalLanguageCommand(command);
        TypeIndividual returnType = typeFactory.getTypeByReflection(returnClass);
        List<TypeIndividual> actualTypeParameters = typeFactory.getTypesByActualValues(parameters);
        List<MethodIndividual> methods = methodFactory.getStaticMethodsBySignature(actualTypeParameters, returnType);
        MethodIndividual bestMethod = new MethodRanker(nlCommand).bestMatch(methods);
        return bestMethod.invokeStatic(returnClass, parameters);
    }

    public <T> T invokeInstanceMethod(String command, Class<T> returnClass, Object instance, Object... parameters) throws Exception {
        NaturalLanguageCommand nlCommand = new NaturalLanguageCommand(command);
        TypeIndividual returnType = typeFactory.getTypeByReflection(returnClass);
        TypeIndividual instanceType = typeFactory.getTypeByReflection(instance.getClass());
        List<TypeIndividual> actualTypeParameters = typeFactory.getTypesByActualValues(parameters);
        List<MethodIndividual> methods = methodFactory.getInstanceMethodsBySignature(instanceType, actualTypeParameters, returnType);
        MethodIndividual bestMethod = new MethodRanker(nlCommand).bestMatch(methods);
        return bestMethod.invoke(instance, returnClass, parameters);
    }

    public Object exec(String command) throws NoFeasibleTreeException {
        return exec(command, Object.class);
    }

    public <T> T exec(String command, Class<T> returnType) throws NoFeasibleTreeException {
        //System.out.println("Building dependency graph...");
        DependencyGraph graph = new DependencyGraph(command);
        System.out.println();
        TaggedDependencyGraph taggedGraph = new TaggedDependencyGraph(graph);
        ExecutionTree executionTree = taggedGraph.findBestFeasibleTree(returnType);
        System.out.println();
        System.out.println("Java Code:");
        System.out.println(executionTree);
        Object result = executionTree.exec();
        if (returnType.isPrimitive()) {
            returnType = Primitives.wrap(returnType);
        }
        try {
            if (result instanceof Number) {
                return returnType.cast(
                        numberConversion(returnType.asSubclass(Number.class), (Number) result)
                );
            }
        } catch (RuntimeException e) {
             // skip
        }
        if (result != null && returnType != null) {
            return returnType.cast(result);
        }
        return null;
    }

    private static Number numberConversion(Class<? extends Number> outputType, Number value) {

        if (value == null) {
            return null;
        }
        if (Byte.class.equals(outputType)) {
            return value.byteValue();
        }
        if (Short.class.equals(outputType)) {
            return value.shortValue();
        }
        if (Integer.class.equals(outputType)) {
            return value.intValue();
        }
        if (Long.class.equals(outputType)) {
            return value.longValue();
        }
        if (Float.class.equals(outputType)) {
            return value.floatValue();
        }
        if (Double.class.equals(outputType)) {
            return value.doubleValue();
        }

        throw new RuntimeException("cannot cast number");

    }

    /*
    public MethodIndividual getStaticMethod(NaturalLanguageCommand nlCommand, TypeIndividual returnType, List<TypeIndividual> parameters) {
        List<MethodIndividual> methods = methodFactory.getStaticMethodsBySignature(parameters, returnType);
        return new MethodRanker(nlCommand).bestMatch(methods);
    }

    public MethodIndividual getInstanceMethod(NaturalLanguageCommand nlCommand,
                                              TypeIndividual declaringClass, TypeIndividual returnType, List<TypeIndividual> parameters) {
        List<MethodIndividual> methods = methodFactory.getInstanceMethodsBySignature(declaringClass, parameters, returnType);
        return new MethodRanker(nlCommand).bestMatch(methods);
    }
    */

    public MethodRanking rankMethods(NaturalLanguageCommand nlCommand, TypeIndividual returnType, List<TypeIndividual> types) {
        List<MethodIndividual> staticMethods = methodFactory.getStaticMethodsBySignature(types, returnType);
        Set<MethodIndividual> methods = new HashSet<>(staticMethods);

        types.forEach(target -> {
            List<TypeIndividual> parameters = new ArrayList<>(types);
            parameters.remove(target);
            List<MethodIndividual> instanceMethods = methodFactory.getInstanceMethodsBySignature(target, parameters, returnType);
            methods.addAll(instanceMethods);

        });
        return new MethodRanker(nlCommand).rank(new ArrayList<>(methods));
    }
}
