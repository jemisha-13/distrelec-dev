/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@code PostCodeToStateMapUtil}
 * 
 * @author datneerajs, Namics AG
 * @since Distrelec 2.0
 */
public class PostCodeToStateMapUtil {

    private static final Logger LOG = LogManager.getLogger(PostCodeToStateMapUtil.class);

    private final static Map<String, StateAbvStratgy> StateAbvStratgyMap = new HashMap<>();
    static {
        StateAbvStratgyMap.put("US", new StateAbvStratgyImpl("/distrelecB2Bcore/data/usa_zip_code_database.csv", "AL"));
        StateAbvStratgyMap.put("CA", new StateAbvStratgyImpl("data/can_zip_code_database.csv", "AB"));
    }

    /**
     * {@code StateAbvStratgy}
     * 
     *
     * @author datneerajs, Namics AG
     * @since Distrelec 2.0
     */
    static interface StateAbvStratgy {
        String getStateAbv(final String postalcode);
    }

    /**
     * @param countryIso
     * @param postalcode
     * @return the abbreviation of the state given by the country and the postal code.
     */
    public static String getStateAbv(final String countryIso, final String postalcode) {
        if (countryIso == null) {
            LOG.warn("null Country ISO-CODE");
            return null;
        }
        final StateAbvStratgy stateAbvStratgy = getCountryStateAbvStratgy(countryIso);
        return stateAbvStratgy != null ? stateAbvStratgy.getStateAbv(postalcode) : null;
    }

    /**
     * @param countryIso
     *            the country ISOCODE
     * @return the {@link StateAbvStratgy} linked to the country ISOCODE
     */
    private static StateAbvStratgy getCountryStateAbvStratgy(final String countryIso) {
        return countryIso != null ? StateAbvStratgyMap.get(countryIso.toUpperCase()) : null;
    }

    /**
     * {@code StateAbvStratgyImpl}
     * 
     *
     * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
     * @since Distrelec 6.0
     */
    final static class StateAbvStratgyImpl implements StateAbvStratgy {

        private final String sourcefileName;
        private final String defaultAbv;

        /**
         * Create a new instance of {@code StateAbvStratgyImpl}
         * 
         * @param sourcefileName
         * @param defaultAbv
         */
        public StateAbvStratgyImpl(final String sourcefileName, final String defaultAbv) {
            this.sourcefileName = sourcefileName;
            this.defaultAbv = defaultAbv;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.namics.distrelec.b2b.core.service.order.util.PostCodeToStateMapUtil.StateAbvStratgy#getStateAbv(java.lang.String)
         */
        @Override
        public String getStateAbv(final String postalcode) {
            try (final Scanner scanner = new Scanner(PostCodeToStateMapUtil.class.getResourceAsStream(sourcefileName))) {
                while (scanner.hasNextLine()) {
                    final String[] split = scanner.nextLine().split(";");
                    if (postalcode.equalsIgnoreCase(split[0])) {
                        return split[1];
                    }
                }
            } catch (final Exception ex) {
                LOG.error("Something went wrong", ex);
            }
            return defaultAbv; // default
        }
    }
}
