package org.codeontology.ned;


import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;
import java.util.stream.Collectors;

public class EntityLinker {
    private final double threshold;
    private Tagger tagger;
    private DBpediaMatcher matcher;

    public EntityLinker() {
        this(0.15);
    }

    public EntityLinker(double threshold) {
        if (threshold < 0 || threshold > 1) {
            throw new IllegalArgumentException("Threshold must be a real number between 0 and 1");
        }
        this.threshold = threshold;
        tagger = new Tagger();
        matcher = new DBpediaMatcher();
    }

    public List<String> linkAndMatchEntities(String text) {
        List<String> annotations = tagger.tag(text).stream()
                .filter(annotation -> annotation.getRho() >= threshold)
                .map(TagMeAnnotation::getTitle)
                .collect(Collectors.toList());

        return matcher.matchAll(annotations);
    }

    public List<String> linkEntities(String text) {
        return tagger.tag(text).stream()
                .filter(annotation -> annotation.getRho() >= threshold)
                .map(TagMeAnnotation::getTitle)
                .map(StringEscapeUtils::unescapeHtml4)
                .map(s -> s.replaceAll(" ", "_"))
                .map(s -> s.replaceAll(",|!|\"|\\?|:", ""))
                .collect(Collectors.toList());

    }
}
