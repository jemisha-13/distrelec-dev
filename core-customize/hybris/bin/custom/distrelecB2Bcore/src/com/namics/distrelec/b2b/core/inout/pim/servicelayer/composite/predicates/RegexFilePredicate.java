package com.namics.distrelec.b2b.core.inout.pim.servicelayer.composite.predicates;

import java.io.File;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class RegexFilePredicate implements Predicate<File> {

    private final Pattern pattern;

    public RegexFilePredicate(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public boolean test(File file) {
        return pattern.matcher(file.getName())
                      .matches();
    }

    public Pattern getPattern() {
        return pattern;
    }
}
