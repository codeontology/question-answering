package org.codeontology.wordvectors;

import org.codeontology.nlp.NLPProcessor;
import org.codeontology.nlp.Tokenizer;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.primitives.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;

public class WordVectors {
    private org.deeplearning4j.models.embeddings.wordvectors.WordVectors wordVectors;
    private int size;

    public WordVectors(String path) {
        load(path);
    }

    public void load(String path) {
        if (path.endsWith(".txt")) {
            loadTxt(path);
        } else {
            loadBin(path);
        }
    }

    public void loadBin(String path) {
        wordVectors = WordVectorSerializer.readWord2VecModel(path);
        size = wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).length;
    }

    public void loadTxt(String path) {
        try {
            Pair<InMemoryLookupTable, VocabCache> pair = WordVectorSerializer.loadTxt(new File(path));
            wordVectors = WordVectorSerializer.fromPair(pair);
            size = wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).length;
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new WordVectorsLoadingException("Could not load word vectors at " + path);
        }
    }

    public INDArray getMeanVector(String sentence) {
        Tokenizer tokenizer = NLPProcessor.getInstance().getTokenizer();
        List<String> tokens = tokenizer.tokenize(sentence);
        return wordVectors.getWordVectorsMean(tokens);
    }

    public INDArray getMeanVector(Collection<String> sentence) {
        return wordVectors.getWordVectorsMean(sentence);
    }

    public INDArray getWordVector(String word) {
        return wordVectors.getWordVectorMatrix(word);
    }

    public int size() {
        return size;
    }
}
