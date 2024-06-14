package com.namics.distrelec.b2b.core.dataimport.batch.util;

import java.io.File;

import de.hybris.platform.acceleratorservices.dataimport.batch.util.SequenceIdParser;

/**
 * Parses which allows unknown values.
 * <p>
 * It was introduced to support backward compatibility because sequence is not provided anymore.
 */
public class DistSequenceIdParser extends SequenceIdParser {

    @Override
    public Long getSequenceId(final File file) {
        final String fileName = file.getName();
        final String part = getParser().parse(fileName, 1);
        if (part != null) {
            return super.getSequenceId(file);
        }
        return null;
    }
}
