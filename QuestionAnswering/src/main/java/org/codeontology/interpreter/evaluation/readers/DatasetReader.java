package org.codeontology.interpreter.evaluation.readers;


import org.codeontology.interpreter.evaluation.data.Dataset;

import java.io.IOException;

public interface DatasetReader<T> {
    Dataset<T> read() throws IOException;
}
