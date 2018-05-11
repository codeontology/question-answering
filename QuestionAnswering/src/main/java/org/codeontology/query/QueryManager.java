package org.codeontology.query;


import org.apache.jena.query.*;
import org.codeontology.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.codeontology.Settings.ENDPOINT;
import static org.codeontology.query.Prefixes.PREFIXES_DECLARATION;

public class QueryManager {

    private static final QueryManager instance = new QueryManager();

    private QueryManager() {

    }

    public static QueryManager getInstance() {
        return instance;
    }

    public ResultSet query(String queryString) {
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.createServiceRequest(ENDPOINT, query)) {
            ResultSet results = qexec.execSelect();
            return ResultSetFactory.copyResults(results) ;
        }
    }

    public ResultSet queryAllProperties(String subjectUri) {
        String query = "SELECT * WHERE {" + Utils.encloseUri(subjectUri) + " ?p ?o . }";
        return query(query);
    }

    public String queryString(String query, String variable) {
        try {
            return query(query).next().get(variable).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> queryStrings(String query, String variable) {
        List<String> results = new ArrayList<>();

        try {
            query(query).forEachRemaining(x -> {
                String current = x.get(variable).toString();
                results.add(current);
            });
        } catch (Exception e) {
            // leave results empty
        }
        return results;
    }

    public String queryValue(String subject, String property) {
        String query = PREFIXES_DECLARATION +
                "SELECT ?value " +
                "WHERE { " +
                Utils.encloseUri(subject) + " " + Utils.encloseUri(property) + " ?value ." +
                "}";

        return queryString(query, "value");
    }

    public List<String> queryValues(String subject, String property) {
        String query = PREFIXES_DECLARATION +
                "SELECT ?value " +
                "WHERE { " +
                Utils.encloseUri(subject) + " " + Utils.encloseUri(property) + " ?value ." +
                "}";

        return queryStrings(query, "value");
    }
}
