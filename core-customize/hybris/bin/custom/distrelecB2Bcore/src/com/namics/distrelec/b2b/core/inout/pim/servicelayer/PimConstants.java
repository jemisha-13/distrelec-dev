/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer;

/**
 * {@code PimConstants}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.10
 */
public final class PimConstants {

    public final static String IMPORT_PRODUCTFEATURES_PARAM = "import.pim.importProductFeatures";
    public final static String IMPORT_FF_DQ_SERVER_INFO_PARAM = "import.pim.importFactFinderDQServerInformations";

    public static final String XP_ATTRIBUTE_ID = "AttributeID";
    public static final String XP_METADATA_VALUE = "MetaData/Value";

    public static final String PIM_IMPORT_CONFIG_CODE = "pimImportConfig";

    // --------------------------------------------------------------
    /**
     * Energy classes
     */
    public static final String EFFICENCY_FEATURE = "Energyclasses_LOV";
    public static final String EFFICENCY_FEATURE_LOWER = EFFICENCY_FEATURE.toLowerCase();

    /**
     * Power
     */
    public static final String POWER_FEATURE = "Leistung_W";
    public static final String POWER_FEATURE_LOWER = POWER_FEATURE.toLowerCase();

    /**
     * Energy classes fitting
     */
    public static final String ENERGY_CLASSES_LUMINAR_FITTING = "Energyclasses_fitting_LOV";
    public static final String ENERGY_CLASSES_LUMINAR_FITTING_LOWER = ENERGY_CLASSES_LUMINAR_FITTING.toLowerCase();

    /**
     * Energy classes for Bulb
     */
    public static final String ENERGY_CLASSES_LUMINAR_INCLUDED_BULB = "energyclasses_included_bulb_LOV";
    public static final String ENERGY_CLASSES_LUMINAR_INCLUDED_BULB_LOWER = ENERGY_CLASSES_LUMINAR_INCLUDED_BULB.toLowerCase();

    /**
     * Energy classes for LED.
     */
    public static final String ENERGY_CLASSES_LUMINAR_BUILT_IN_LED = "Energyclasses_built-in_LED_LOV";
    public static final String ENERGY_CLASSES_LUMINAR_BUILT_IN_LED_LOWER = ENERGY_CLASSES_LUMINAR_BUILT_IN_LED.toLowerCase();

    // --------------------------------------------------------------

    /**
     * Create a new instance of {@code PimConstants}
     */
    private PimConstants() {
        super();
    }
}
