package org.codeontology.individuals;


import com.google.common.primitives.Primitives;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.jena.query.ResultSet;
import org.codeontology.Ontology;
import org.codeontology.query.Prefixes;
import org.codeontology.query.QueryManager;
import org.codeontology.query.TypeFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static org.codeontology.query.Prefixes.DBR;

public class MethodIndividual extends NamedElementIndividual {

    private Class<?>[] parameterClasses;
    private List<TypeIndividual> formalTypeParameters;
    private TypeIndividual returnType;
    private TypeIndividual declaringClass;
    private String returnDescription;
    private String comment;
    private List<String> dbpediaLinks;
    private Method reflectedMethod;
    private List<String> modifierUris;

    public MethodIndividual(String uri) {
        super(uri);
    }

    public <T> T invokeStatic(Class<T> returnType, Object... parameters) throws Exception {
        return invoke(null, returnType, parameters);
    }

    public <T> T invoke(Object target, Class<T> returnType, Object... parameters) throws Exception {
        Object[] sortedParameters = sortParameters(parameters);
        Object result =  reflect().invoke(target, sortedParameters);
        if (returnType.isPrimitive()) {
            returnType = Primitives.wrap(returnType);
        }
        if (result != null && returnType != null) {
            return returnType.cast(result);
        }
        return null;
    }

    public Object invoke(Object... args) throws Exception {
        if (isStatic()) {
            return invokeStatic(args);
        }

        int index = getTargetIndex(args);
        Object target = args[index];
        Object[] parameters = ArrayUtils.remove(args, index);
        Object[] sortedParameters = sortParameters(parameters);
        return reflect().invoke(target, sortedParameters);
    }

    public Object invokeStatic(Object... args) throws Exception {
        Object[] sortedParameters = sortParameters(args);
        return reflect().invoke(null, sortedParameters);
    }

    private int getTargetIndex(Object... args) {
        List<TypeIndividual> types = TypeFactory.getInstance()
                .getTypesByActualValues(args);
        return guessInstanceIndex(types);
    }

    public int guessInstanceIndex(List<TypeIndividual> types) {
        int i = 0;
        TypeIndividual targetType = getDeclaringClass();
        for (TypeIndividual type : types) {
            if (type.getImplicitlyAssignableTypes().contains(targetType)) {
                return i;
            }
            i++;
        }
        throw new NoSuchElementException("Target not found for instance method: " + this + " and types: " + types);
    }

    private Object[] sortParameters(Object... parameters) {
        queryParameterClasses();
        Set<Object> used = new HashSet<>();
        return Arrays.stream(parameterClasses).map(type -> {
            Object match = Arrays.stream(parameters).filter(parameter -> {
                if (used.contains(parameter)) {
                    return false;
                }
                Class<?> clazz = type;
                if (Primitives.isWrapperType(clazz)) {
                    clazz = Primitives.unwrap(clazz);
                }
                Class<?> parameterClass = parameter.getClass();
                if (Primitives.isWrapperType(parameterClass)) {
                    parameterClass = Primitives.unwrap(parameterClass);
                }
                return ClassUtils.isAssignable(parameterClass, clazz, true);
            }).findFirst().get();
            used.add(match);
            return match;
        }).toArray();
    }

    private Method reflect() throws Exception {
        if (reflectedMethod != null) {
            return reflectedMethod;
        }

        queryDeclaringClass();
        queryParameterClasses();
        String declaringClassName = declaringClass.getCanonicalName();
        Class<?> declaringClass = Class.forName(declaringClassName);
        reflectedMethod = declaringClass.getMethod(getName(), parameterClasses);
        return reflectedMethod;
    }

    public List<TypeIndividual> getFormalTypeParameters() {
        if (formalTypeParameters != null) {
            return formalTypeParameters;
        }

        formalTypeParameters = new ArrayList<>();

        String queryString = Prefixes.PREFIXES_DECLARATION +
                "SELECT ?type " +
                "WHERE { " +
                getEnclosedUri() + " woc:hasParameter/woc:hasType ?type ."  +
                "}";

        ResultSet resultset = QueryManager.getInstance().query(queryString);

        resultset.forEachRemaining(record -> {
            String typeUri = record.get("type").toString();
            TypeIndividual type = new TypeIndividual(typeUri);
            formalTypeParameters.add(type);
        });

        return formalTypeParameters;
    }

    public TypeIndividual getReturnType() {
        if (returnType == null) {
            String typeUri = valueOf(Ontology.RETURN_TYPE_PROPERTY);
            returnType = new TypeIndividual(typeUri);
        }

        return returnType;
    }

    private void queryParameterClasses() {
        if (parameterClasses != null) {
            return;
        }

        parameterClasses = getFormalTypeParameters().stream()
                .map(TypeIndividual::reflect).toArray(Class[]::new);
    }

    private String queryDeclaringClassURI() {
        return valueOf(Ontology.DECLARED_BY_PROPERTY);
    }

    private void queryDeclaringClass() {
        if (declaringClass != null) {
            return;
        }
        String declaringClassUri = queryDeclaringClassURI();
        declaringClass = new TypeIndividual(declaringClassUri);
    }

    public List<String> getDBpediaLinks() {
        if (dbpediaLinks == null) {
            dbpediaLinks = valuesOf(Ontology.DUL_ASSOCIATED_WITH_PROPERTY).stream()
                    .map(link -> link.replaceAll(DBR + "|<|>", "")).collect(Collectors.toList());
        }

        return dbpediaLinks;
    }

    public String getComment() {
        if (comment == null) {
            comment = valueOf(Ontology.COMMENT_PROPERTY);
        }
        return comment;
    }


    public String getReturnDescription() {
        if (returnDescription == null) {
            returnDescription = valueOf(Ontology.RETURN_DESCRIPTION_PROPERTY);
        }
        return returnDescription;
    }

    public String getDescription() {
        String returnDescription = getReturnDescription();
        String comment = getComment();

        if (returnDescription != null && !returnDescription.trim().equals("")) {
            return returnDescription;
        }

        return comment;
    }

    public List<String> getModifierUris() {
        if (modifierUris == null) {
            modifierUris = valuesOf(Ontology.MODIFIER_PROPERTY);
        }
        return modifierUris;
    }

    public boolean isStatic() {
        String staticUri = Ontology.STATIC_INDIVIDUAL.getURI();
        return getModifierUris().contains(staticUri);
    }

    public TypeIndividual getDeclaringClass() {
        queryDeclaringClass();
        return declaringClass;
    }

    public Class<?>[] getParameterClasses() {
        queryParameterClasses();
        return parameterClasses;
    }

    public boolean isVoid() {
        TypeIndividual voidType = new TypeIndividual(Prefixes.WOC + "Void");
        return getReturnType().equals(voidType);
    }

    public String getQualifiedName() {
        return getDeclaringClass().getCanonicalName() + "." + getName();
    }
}
