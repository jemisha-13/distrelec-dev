package com.namics.distrelec.b2b.core.dataimport.batch.converter.impl;

import java.util.Map;

public class DefaultDistPunchoutFilterCOImpexConverter extends DefaultDistImpexConverter {

    private static final String GB = "GB";
    private static final String XI = "XI";

    @Override
    public String convert(final Map<Integer, String> row, final Long sequenceId, final String erpSequenceId) {
        StringBuilder impexLine = new StringBuilder(super.convert(row, sequenceId, erpSequenceId));

        //[DISTRELEC-28011] if GB add additional filter for Northern Ireland (XI)
        if (GB.equals(row.get(1))) {
            row.put(1, XI);
            impexLine.append("\n").append(super.convert(row, sequenceId, erpSequenceId));
        }
        return impexLine.toString();
    }
}
