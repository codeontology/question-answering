package org.codeontology.individuals;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.primitives.Primitives;
import org.apache.commons.lang3.ClassUtils;
import org.codeontology.Ontology;
import org.codeontology.Utils;
import org.codeontology.query.TypeFactory;

import java.util.*;
import java.util.stream.Collectors;

public class TypeIndividual extends NamedElementIndividual {
    private Class<?> reflectedType;
    private String canonicalName;
    private TypeIndividual genericType;
    private String packageName;

    private static Multimap<Class<?>, Class<?>> implicitPrimitiveMap = buildImplicitPrimitiveMap();

    private static Multimap<Class<?>, Class<?>> buildImplicitPrimitiveMap() {
        Multimap<Class<?>, Class<?>> result = MultimapBuilder.hashKeys().hashSetValues().build();
        result.put(short.class, int.class);
        result.put(int.class, long.class);
        result.put(int.class, float.class);
        result.put(long.class, double.class);
        result.put(float.class, double.class);
        return result;
    }


    public TypeIndividual(String uri) {
        super(uri);
    }

    private Class<?> buildReflectedType()  {
        if (getGenericType() != null) {
            return genericType.buildReflectedType();
        }

        String name = getCanonicalName();
        if (name == null) {
            name = getName();
        }
        try {
            return ClassUtils.getClass(name);
        } catch (Exception e) {
            try {
                name = getPackageName() + "." + name;
                return ClassUtils.getClass(name);
            } catch (Exception e2) {
                return Object.class;
            }
        }
    }

    public String getPackageName() {
        if (packageName == null) {
            String propertyPath = "<" + Ontology.HAS_PACKAGE_PROPERTY +">/<" + Ontology.NAME_PROPERTY + ">";
            packageName = valueOf(propertyPath);
        }

        return packageName;
    }

    public String getCanonicalName() {
        queryCanonicalName();
        return canonicalName;
    }

    private void queryCanonicalName() {
        if (canonicalName == null) {
            canonicalName = valueOf(Ontology.CANONICAL_NAME_PROPERTY);
        }
    }

    public Class<?> reflect() {
        if (reflectedType == null) {
            reflectedType = buildReflectedType();
        }
        return reflectedType;
    }

    public boolean isPrimitive() {
        return reflect().isPrimitive();
    }

    public boolean isPrimitiveWrapper() {
        Class<?> type = reflect();
        return Primitives.isWrapperType(type);
    }

    public boolean isPrimitiveOrWrapper() {
        return isPrimitive() || isPrimitiveWrapper();
    }

    public List<TypeIndividual> getSuperTypes() {
        return valuesOf(Utils.encloseUri(Ontology.EXTENDS_PROPERTY.toString()) + "+").stream()
                .map(TypeIndividual::new).collect(Collectors.toList());
    }


    public List<TypeIndividual> getGenericSuperTypes() {
        String propertyPath = "(" + Utils.encloseUri(Ontology.GENERIC_TYPE_PROPERTY.toString()) + "?/" +
                Utils.encloseUri(Ontology.EXTENDS_PROPERTY.toString()) + "*/" +
                Utils.encloseUri(Ontology.IMPLEMENTS_PROPERTY.toString()) + "*)+";
        return valuesOf(propertyPath).stream()
                .map(TypeIndividual::new).collect(Collectors.toList());
    }

    public TypeIndividual unwrap() {
        Class<?> primitive = Primitives.unwrap(reflect());
        return TypeFactory.getInstance().getTypeByReflection(primitive);
    }

    public TypeIndividual wrap() {
        Class<?> wrapper = Primitives.wrap(reflect());
        return TypeFactory.getInstance().getTypeByReflection(wrapper);
    }

    public Set<TypeIndividual> getImplicitlyAssignableTypes() {
        Set<TypeIndividual> result = new HashSet<>();
        result.add(this);

        TypeIndividual clazz = this;
        TypeIndividual primitive = null;

        if (isPrimitive()) {
            primitive = this;
            clazz = wrap();
            result.add(clazz);
        }

        if (isPrimitiveWrapper()) {
            primitive = unwrap();
            result.add(primitive);
        }

        if (primitive != null) {
            Collection<Class<?>> values = implicitPrimitiveMap.get(primitive.reflect());
            List<TypeIndividual> types = TypeFactory.getInstance().getTypesByReflection(values);
            types.forEach(t -> result.addAll(t.getImplicitlyAssignableTypes()));
        }

        result.addAll(clazz.getGenericSuperTypes());

        return result;
    }

    public TypeIndividual getGenericType() {
        if (genericType == null) {
            String genericTypeUri = valueOf(Ontology.GENERIC_TYPE_PROPERTY);
            if (genericTypeUri != null) {
                genericType = new TypeIndividual(genericTypeUri);
            }
        }

        return genericType;
    }
}
