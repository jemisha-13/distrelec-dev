package com.namics.distrelec.b2b.core.inout.pim.servicelayer.composite;

import com.namics.distrelec.b2b.core.inout.pim.PimExportParser;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;

import java.io.File;

public abstract class AbstractCompositeEntry {

    private final PimExportParser<? extends ImportContext> parser;

    private final int order;

    public AbstractCompositeEntry(PimExportParser<? extends ImportContext> parser, int order) {
        if (parser == null) {
            throw new IllegalArgumentException("Parser can not be null!");
        }
        this.parser = parser;
        this.order = order;
    }

    public abstract boolean isSupported(File file);

    public int getOrder() {
        return order;
    }

    public PimExportParser<? extends ImportContext> getParser() {
        return parser;
    }
}
