package org.codeontology.individuals;

import org.codeontology.Utils;
import org.codeontology.ned.EntityLinker;
import org.codeontology.query.MethodFactory;
import org.codeontology.query.TypeFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.codeontology.query.Prefixes.WOC;

public class Test {
    public static void main(String[] args) throws Exception {
        Utils.configureLog();

        testMethodCallByURI();
        System.out.println();
        testTypeCreation();
        System.out.println();
        testMethodCreation();
        System.out.println();
        testDBpediaLinks();
        System.out.println();
        testTagMeAnnotations();
        System.out.println();
        testImplicitlyAssignablePrimitives();

    }

    private static void testImplicitlyAssignablePrimitives() {
        TypeIndividual t = TypeFactory.getInstance().getTypeByReflection(Long.class);
        t.getImplicitlyAssignableTypes().forEach(System.out::println);
    }

    private static void testMethodCreation() {
        TypeIndividual t0 = TypeFactory.getInstance().getTypeByReflection(int[].class);
        TypeIndividual t1 = TypeFactory.getInstance().getTypeByReflection(int.class);
        TypeIndividual returnType = TypeFactory.getInstance().getTypeByReflection(void.class);
        List<MethodIndividual> results = MethodFactory.getInstance().getStaticMethodsByParameters(Arrays.asList(t0, t1));
        int end = Math.min(results.size(), 30);
        results.subList(0, end).forEach(System.out::println);
    }

    private static void testTypeCreation() {
        List<String> l = new ArrayList<>();
        TypeIndividual arrayListType = TypeFactory.getInstance().getTypeByReflection(l.getClass());

        Object[] objectArray = new Object[5];
        TypeIndividual objectArrayType = TypeFactory.getInstance().getTypeByReflection(objectArray.getClass());

        int[] intArray = new int[5];
        TypeIndividual intArrayType = TypeFactory.getInstance().getTypeByReflection(intArray.getClass());

        TypeIndividual intType = TypeFactory.getInstance().getTypeByReflection(int.class);

        List<TypeIndividual> types = Arrays.asList(arrayListType, objectArrayType, intArrayType, intType);
        System.out.println(types);
    }

    private static void testDBpediaLinks() {
        String cbrtUri = WOC + "java.lang.Math-cbrt(double)";
        MethodIndividual cbrt = new MethodIndividual(cbrtUri);
        cbrt.getDBpediaLinks().forEach(System.out::println);
    }

    private static void testTagMeAnnotations() {
        String cbrtUri = WOC + "java.lang.Math-cbrt(double)";
        MethodIndividual cbrt = new MethodIndividual(cbrtUri);
        String comment = cbrt.getComment();
        List<String> annotations = new EntityLinker().linkEntities(comment);
        annotations.forEach(System.out::println);
    }

    private static void testMethodCallByURI() throws Exception {
        double[] values = new double[10];
        new MethodIndividual(WOC + "java.util.Arrays-fill(double[]-double)").invokeStatic(void.class, values, 73);
        Arrays.stream(values).forEach(System.out::println);

        System.out.println();

        String message = "CONVERT TO LOWERCASE";
        String result = new MethodIndividual(WOC + "java.lang.String-toLowerCase()").invoke(message, String.class);
        System.out.println(result);
    }
}
