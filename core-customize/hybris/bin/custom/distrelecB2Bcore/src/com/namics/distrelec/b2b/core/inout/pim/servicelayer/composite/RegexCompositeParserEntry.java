package com.namics.distrelec.b2b.core.inout.pim.servicelayer.composite;

import com.namics.distrelec.b2b.core.inout.pim.PimExportParser;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.composite.predicates.RegexFilePredicate;

import java.io.File;

public class RegexCompositeParserEntry extends AbstractCompositeEntry {

    private final RegexFilePredicate supports;

    public RegexCompositeParserEntry(RegexFilePredicate supports, int order, PimExportParser<? extends ImportContext> parser) {
        super(parser, order);
        if (supports == null) {
            throw new IllegalArgumentException("RegexFilePredicate can not be null!");
        }
        this.supports = supports;
    }

    @Override
    public boolean isSupported(File file) {
        return supports.test(file);
    }

}
