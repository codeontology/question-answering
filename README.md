# Question Answering over CodeOntology

This system leverages [CodeOntology](http://codeontology.org) to translate complex natural language questions into an appropriate Java source code.

For instance, the question:

*What is the cube root of the max between 20 and 27?*

is translated into the following Java source code:
```Java
java.lang.Math.cbrt(java.lang.Double.max(20, 27))
```
which is executed to retrieve the desired answer.

## CodeOntology

First, be sure that [Fuseki](https://jena.apache.org/documentation/fuseki2/) is running at localhost:3030. If you don't have Fuseki installed, you can get it by following the instructions available [here](https://jena.apache.org/documentation/fuseki2/).

Next, you should download the dataset extracted by applying CodeOntology to the OpenJDK 8 project. It is available on [Zenodo](https://doi.org/10.5281/zenodo.785550) under CC BY 4.0 license.

Create a new dataset named OpenJDK on Fuseki and load all the files you have downloaded from Zenodo to the newly created dataset.

Now, you should be able to use Fuseki to run simple queries on CodeOntology. For instance, you can select all methods from OpenJDK computing the cube root of a real value by running the following SPARQL query:

```SPARQL
SELECT ?method
WHERE {
  ?method a woc:Method ;
          woc:hasParameter/woc:hasType woc:Double ; 
          dul:associatedWith dbr:Cube_root .
}
```

## Word2Vec

This system makes use of a pre-trained [Word2Vec](https://code.google.com/archive/p/word2vec/) model. You can download it from [Google Drive](https://drive.google.com/file/d/0B7XkCwpI5KDYNlNUTTlSS21pQmM/edit) or simply run the following script, which will place the file in ```nlp/WordVectors```:

```bash
$ ./getW2V.sh
```

## Build and Run
The system is built with Maven. Be sure to use OpenJDK 8 (incompatibility with JDK 10 is known). Hence, you only have to run the following commands:

```bash
$ cd QuestionAnswering
$ mvn package
```

Now, you are ready to run the tool:

```bash
$ ../askCO
```
