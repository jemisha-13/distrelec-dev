package com.namics.distrelec.b2b.core.keygenerator.impl;

import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;

public class DistPersistentKeyGenerator extends PersistentKeyGenerator {

    private static final String RESET_DATE = "20211024000000";

    @Override
    public void setStart(String start) {
        String cleanedStart = start.replace(" ", "");
        if (cleanedStart.length() >= 12) {
            // assuming it is date-time, adds seconds
            cleanedStart += "00";

            long resetDateLong = Long.valueOf(RESET_DATE);
            long startLong = Long.valueOf(cleanedStart);
            if (startLong > resetDateLong) {
                cleanedStart = Long.toString(startLong - resetDateLong, 36);
            }
        }
        super.setStart(cleanedStart);
    }
}
