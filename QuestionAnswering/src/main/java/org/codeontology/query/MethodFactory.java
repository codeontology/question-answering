package org.codeontology.query;

import org.apache.jena.query.ResultSet;
import org.codeontology.individuals.MethodIndividual;
import org.codeontology.individuals.TypeIndividual;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.codeontology.query.Prefixes.PREFIXES_DECLARATION;

public class MethodFactory {
    private static MethodFactory instance = new MethodFactory();

    public static MethodFactory getInstance() {
        return instance;
    }

    private MethodFactory() {
    }

    public List<MethodIndividual> getStaticMethodsBySignature(List<TypeIndividual> parameters, TypeIndividual returnType) {
        String query = buildMethodQuery(null, returnType, mapToSingleton(parameters), true);
        return getMethodsByQuery(query);
    }

    public List<MethodIndividual> getMethodsBySignature(List<TypeIndividual> parameters, TypeIndividual returnType) {
        String query = buildMethodQuery(null, returnType, mapToSingleton(parameters), false);
        return getMethodsByQuery(query);
    }

    public List<MethodIndividual> getStaticMethodsByParameters(List<TypeIndividual> formalTypeParameters) {
        return getStaticMethodsBySignature(formalTypeParameters, null);
    }

    public List<MethodIndividual> getInstanceMethodsBySignature(TypeIndividual instance,
                                                                List<TypeIndividual> parameters, TypeIndividual returnType) {
        String query = buildMethodQuery(instance, returnType, mapToSingleton(parameters), false);
        return getMethodsByQuery(query);
    }

    public List<MethodIndividual> getMatchingMethods(List<Set<TypeIndividual>> argumentTypes, TypeIndividual returnType) {
        Set<MethodIndividual> resultSet = new HashSet<>();

        String staticQuery = buildMethodQuery(null, returnType, argumentTypes, true);
        List<MethodIndividual> staticMethods = getMethodsByQuery(staticQuery);

        resultSet.addAll(staticMethods);

        argumentTypes.forEach(set -> set.forEach(target -> {
            List<Set<TypeIndividual>> parameters = new ArrayList<>(argumentTypes);
            parameters.remove(set);
            String query = buildMethodQuery(target, returnType, parameters, false);
            List<MethodIndividual> instanceMethods = getMethodsByQuery(query);
            resultSet.addAll(instanceMethods);
        }));

        return new ArrayList<>(resultSet);
    }

    private List<Set<TypeIndividual>> mapToSingleton(List<TypeIndividual> parameters) {
        return parameters.stream()
                .map(Collections::singleton)
                .collect(Collectors.toList());
    }

    private String buildMethodQuery(TypeIndividual instance, TypeIndividual returnType, List<Set<TypeIndividual>> parameters, boolean staticMethod) {
        String parameterVariables = IntStream.range(0, parameters.size())
                .mapToObj(i -> "?p" + i)
                .collect(Collectors.joining(", "));

        if (!parameterVariables.equals("")) {
            parameterVariables = "?method woc:hasParameter " + parameterVariables + " .\n";
        }

        String typePattern = IntStream.range(0, parameters.size())
                .mapToObj(i -> "?p" + i + " woc:hasType ?t" + i)
                .collect(Collectors.joining(".\n"));

        if (!typePattern.equals("")) {
            typePattern = typePattern + ".\n";
        }

        String instancePattern = "";

        if (staticMethod) {
            instancePattern = "?method woc:hasModifier woc:Static .\n";
        } else if (instance != null) {
            instancePattern = "?method woc:isDeclaredBy " + instance + " .\n";
        }

        String filterDifferentParameters = buildDifferentParametersFilter(parameters.size());

        return PREFIXES_DECLARATION +
                "SELECT DISTINCT ?method\n" +
                "WHERE {\n" +
                "?method a woc:Method .\n" +
                "?method woc:hasModifier woc:Public . \n" +
                instancePattern +
                parameterVariables +
                typePattern +
                //buildClassParametersPattern(parameters) +
                buildReturnPattern(returnType) +
                buildTypeFilters(parameters) +
                filterDifferentParameters +
                "FILTER NOT EXISTS { ?method woc:hasParameter/woc:hasPosition " + parameters.size() + " } }\n";
    }

    private String buildDifferentParametersFilter(int size) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                String currentFilter = "FILTER (?p" + i + " != ?p" + j + ")\n";
                builder.append(currentFilter);
            }
        }
        return builder.toString();
    }

    private List<MethodIndividual> getMethodsByQuery(String query) {
        ResultSet resultSet = QueryManager.getInstance().query(query);

        List<MethodIndividual> methods = new ArrayList<>();
        resultSet.forEachRemaining(record -> methods.add(
                new MethodIndividual(record.get("method").toString())
        ));
        return methods;
    }

    private String buildReturnPattern(TypeIndividual returnType) {
        if (returnType == null) {
            return "";
        }

        String base = "?method woc:hasReturnType ?returnType .\n";

        if (returnType.isPrimitiveOrWrapper()) {
            String types = returnType.getImplicitlyAssignableTypes().stream()
                    .map(TypeIndividual::toString)
                    .collect(Collectors.joining(", "));
            return base + "FILTER ( ?returnType IN ( " + types + " ) )\n";
        }

        return base + "?returnType (woc:hasGenericType?/woc:extends*/woc:implements?)+ " + returnType + " .\n";
    }

    private String buildTypeFilters(List<Set<TypeIndividual>> parameters) {
        return IntStream.range(0, parameters.size())
                    //.filter(i -> parameters.get(i).isPrimitiveOrWrapper())
                    .mapToObj(i -> {
                            Set<TypeIndividual> types = parameters.get(i);
                            String uris = types.stream()
                                    .flatMap(t -> t.getImplicitlyAssignableTypes().stream())
                                    .map(TypeIndividual::toString)
                                    .collect(Collectors.joining(", "));
                            return "FILTER ( ?t" + i + " IN ( " + uris + " ) )";
                    })
                    .collect(Collectors.joining("\n")) + "\n";
    }

//    private String buildClassParametersPattern(List<TypeIndividual> parameters) {
//        if (parameters.isEmpty()) {
//            return "";
//        }
//
//        String result = IntStream.range(0, parameters.size())
//                .filter(i -> !parameters.get(i).isPrimitiveOrWrapper())
//                .mapToObj(i -> parameters.get(i) + " " +
//                        "(woc:hasGenericType?/woc:extends*/woc:implements?)+ " +
//                        "?t" + i)
//                .collect(Collectors.joining(" .\n"));
//
//        if (!result.equals("")) {
//            result = result + ".\n";
//        }
//
//        return result;
//    }
}
