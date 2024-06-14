package com.distrelec.fusionsearch.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * Global class for all Distrelecfusionsearch constants. You can add global constants for your extension into this
 * class.
 */
public final class DistrelecfusionsearchConstants extends GeneratedDistrelecfusionsearchConstants {
    public static final String EXTENSIONNAME = "distrelecfusionsearch";

    public static final String CATEGORY_CODES_FACET_CODE = "categoryCodes";

    public static final String MANUFACTURER_NAME_COLUMN = "manufacturerName_s";

    public static final String RELEVANCE_SORT = StringUtils.EMPTY; // empty string serves as best match/relevance

    public static final String MANUFACTURER_SORT = "Manufacturer";

    public static final String PRICE_SORT = "Price";

    public static final String SINGLE_MIN_PRICE_GROSS_COLUMN = "singleMinPriceGross_d";

    private DistrelecfusionsearchConstants() {
        // empty to avoid instantiating this constant class
    }

    public static class FusionSearchParameters {

        public static final String CHANNEL_PARAM = "channel";

        public static final String COUNTRY_PARAM = "country";

        public static final String FQ_PARAM = "fq";

        public static final String LANGUAGE_PARAM = "language";

        public static final String MODE_PARAM = "mode";

        public static final String QUERY_PARAM = "q";

        public static final String SESSION_PARAM = "session";

        public static final String SORT_PARAM = "sort";

        private FusionSearchParameters() {
            // empty to avoid instantiating this constant class
        }
    }

    public static class FusionSessionParams {

        public static final String LAST_PAGE_SIZE = "fusionLastPageSize";

        private FusionSessionParams() {
            // empty to avoid instantiating this constant class
        }
    }
}
