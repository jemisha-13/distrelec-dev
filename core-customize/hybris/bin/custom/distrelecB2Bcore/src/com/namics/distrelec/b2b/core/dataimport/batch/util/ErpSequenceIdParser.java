package com.namics.distrelec.b2b.core.dataimport.batch.util;

import java.io.File;

import de.hybris.platform.acceleratorservices.util.RegexParser;

public class ErpSequenceIdParser {

    private RegexParser parser;

    public String getErpSequenceId(final File file) {
        final String fileName = file.getName();
        final String part = parser.parse(fileName, 1);
        if (part != null) {
            return part;
        } else {
            throw new IllegalArgumentException("missing sequenceId in " + fileName);
        }
    }

    /**
     * @param parser
     *           the parser to set
     */
    public void setParser(final RegexParser parser)
    {
        this.parser = parser;
    }

    /**
     * @return the parser
     */
    protected RegexParser getParser()
    {
        return parser;
    }
}
