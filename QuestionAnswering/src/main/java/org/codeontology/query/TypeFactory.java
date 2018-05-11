package org.codeontology.query;

import org.codeontology.individuals.TypeIndividual;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.codeontology.query.Prefixes.PREFIXES_DECLARATION;

public class TypeFactory {
    private static TypeFactory instance = new TypeFactory();
    private QueryManager queryManager = QueryManager.getInstance();

    public static TypeFactory getInstance() {
        return instance;
    }


    private TypeFactory() {

    }

    public TypeIndividual getTypeByReflection(Class<?> clazz) {
        if (clazz.isArray()) {
            return getArrayByReflection(clazz);
        }
        if (clazz.isPrimitive()) {
            return getPrimitiveByReflection(clazz);
        }
        return getClassByReflection(clazz);
    }

    public TypeIndividual getTypeByActualValue(Object value) {
        return getTypeByReflection(value.getClass());
    }

    public List<TypeIndividual> getTypesByReflection(Collection<Class<?>> classes) {
        return classes.stream().map(this::getTypeByReflection).collect(Collectors.toList());
    }

    public List<TypeIndividual> getTypesByActualValues(Object... values) {
        return Arrays.stream(values)
                .map(this::getTypeByActualValue)
                .collect(Collectors.toList());
    }

    private TypeIndividual getClassByReflection(Class<?> clazz) {
        String query =
                PREFIXES_DECLARATION +
                        "SELECT ?class " +
                        "WHERE {" +
                        "?class woc:hasCanonicalName \"" + clazz.getCanonicalName() + "\" }";
        String uri = queryManager.queryString(query, "class");
        return new TypeIndividual(uri);
    }

    private TypeIndividual getPrimitiveByReflection(Class<?> primitive) {
        String query =
                PREFIXES_DECLARATION +
                        "SELECT ?primitive " +
                        "WHERE { " +
                        "?primitive a woc:PrimitiveType ; woc:hasName \"" + primitive.getCanonicalName() + "\" }";
        String uri = queryManager.queryString(query, "primitive");
        return new TypeIndividual(uri);
    }

    private TypeIndividual getArrayByReflection(Class<?> array) {
        String query =
                PREFIXES_DECLARATION +
                "SELECT ?array " +
                "WHERE { " +
                "?array a woc:ArrayType ; woc:hasName \"" + array.getCanonicalName() + "\" }";
        String uri = queryManager.queryString(query, "array");
        return new TypeIndividual(uri);
    }

}
