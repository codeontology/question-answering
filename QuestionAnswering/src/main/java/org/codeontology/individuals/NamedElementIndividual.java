package org.codeontology.individuals;

import org.codeontology.Ontology;

public abstract class NamedElementIndividual extends CodeElementIndividual {

    private String name;
    private String label;

    public NamedElementIndividual(String uri) {
        super(uri);
    }

    public String getName() {
        if (name == null) {
            name = valueOf(Ontology.NAME_PROPERTY);
        }
        return name;
    }

    public String getLabel() {
        if (label == null) {
            label = valueOf(Ontology.RDFS_LABEL_PROPERTY);
        }
        return label;
    }
}
