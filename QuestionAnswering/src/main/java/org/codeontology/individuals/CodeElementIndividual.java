package org.codeontology.individuals;

import org.apache.jena.rdf.model.Property;
import org.codeontology.Utils;
import org.codeontology.query.QueryManager;

import java.util.List;

import static org.codeontology.query.Prefixes.WOC;

public abstract class CodeElementIndividual {

    private final String uri;

    public CodeElementIndividual(String uri) {
        if (uri == null || uri.trim().equals("")) {
            throw new IllegalArgumentException("URI cannot be null or empty");
        }

        String enclosed = Utils.encloseUri(uri);

        if (enclosed.startsWith("<" + WOC)) {
            this.uri = uri;
        } else {
            this.uri = WOC + uri;
        }
    }

    public String valueOf(Property property) {
        return valueOf(property.toString());
    }

    public String valueOf(String property) {
        return QueryManager.getInstance().queryValue(uri, property);
    }

    public List<String> valuesOf(String property) {
        return QueryManager.getInstance().queryValues(uri, property);
    }

    public List<String> valuesOf(Property property) {
        return valuesOf(property.toString());
    }

    public String getEnclosedUri() {
        return Utils.encloseUri(uri);
    }

    public String getUri() {
        return uri;
    }

    public String getRelativeUri() {
        return uri.replaceAll("(" + WOC + ")|<|>", "");
    }

    @Override
    public String toString() {
        return getEnclosedUri();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodeElementIndividual)) return false;

        CodeElementIndividual that = (CodeElementIndividual) o;

        return uri.equals(that.uri);
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }
}
