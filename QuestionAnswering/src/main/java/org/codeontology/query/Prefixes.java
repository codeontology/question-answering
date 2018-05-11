package org.codeontology.query;

public class Prefixes {
    public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    public static final String WOC = "http://rdf.webofcode.org/woc/";
    public static final String DUL = "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#";
    public static final String DBR = "http://dbpedia.org/resource/";

    public static final String WOC_DECLARATION = "PREFIX woc: <" + WOC + ">\n";
    public static final String RDF_DECLARATION = "PREFIX rdf: <" + RDF + ">\n";
    public static final String RDFS_DECLARATION = "PREFIX rdfs: <" + RDFS + ">\n";
    public static final String DUL_DECLARATION = "PREFIX dul: <" + DUL + ">\n";
    public static final String DBR_DECLARATION = "PREFIX dbr: <" + DBR + ">\n";

    public static final String PREFIXES_DECLARATION = WOC_DECLARATION + RDF_DECLARATION +
            RDFS_DECLARATION + DUL_DECLARATION + DBR_DECLARATION;
}
