package org.codeontology.interpreter.evaluation.data;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Dataset<T> {
    private List<T> data;

    public Dataset() {
        data = new ArrayList<>();
    }

    public Dataset(int initialCapacity) {
        data = new ArrayList<>(initialCapacity);
    }

    public Dataset(Collection<? extends T> c) {
        data = new ArrayList<>(c);
    }


    public Dataset(Dataset<? extends T> other) {
        data = new ArrayList<>(other.data);
    }

    public List<T> toList() {
        return new ArrayList<>(data);
    }

    public List<T> asList() {
        return data;
    }

    public Stream<T> stream() {
        return data.stream();
    }

    public boolean add(T labeledText) {
        return data.add(labeledText);
    }

    public boolean addAll(Dataset<? extends T> c) {
        return data.addAll(c.data);
    }

    public int size() {
        return data.size();
    }

    public void shuffle(Random prg) {
        Collections.shuffle(data, prg);
    }

    public Dataset<T> sample(int fromIndex, int toIndex) {
        List<T> sample = data.subList(fromIndex, toIndex);
        return new Dataset<>(sample);
    }

    public List<Dataset<T>> split(List<Double> values) {
        if (values.stream().reduce((a, b) -> a + b).orElse(0.0) > 1.0) {
            throw new IllegalArgumentException("The sum of the values used for splitting the dataset must not be higher than 1.0");
        }

        List<Dataset<T>> result = new ArrayList<>(values.size());
        int i = 0;

        for (Double value : values) {
            int j = i + (int) (value * size());
            result.add(sample(i, j));
            i = j;
        }

        return result;
    }

    public void forEach(Consumer<? super T> action) {
        data.forEach(action);
    }
}
