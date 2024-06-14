/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.product;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;

import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants.*;

/**
 * {@code Constants}
 * <p>
 * A utility class holding the names of some important {@link ClassAttributeAssignmentModel}'s qualifiers.
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.19
 */
public class Constants {

    /**
     * UNSPSC_5
     */
    public static final String UNSPSC_5 = "class-root.unspsc5";
    public static final String UNSPSC_5_LOWER = UNSPSC_5.toLowerCase();

    /**
     * SVHC URL
     */
    public static final String SVHC_URL = "svhc_url_lov";
    public static final String SVHC_URL_LOWER = SVHC_URL.toLowerCase();

    /**
     * ROHS URL
     */
    public static final String ROHS_URL_TXT = "rohsurl_txt";
    public static final String ROHS_URL_TXT_LOWER = ROHS_URL_TXT.toLowerCase();

    /**
     * DANGEROUS SUBSTANCE DIRECTIVE
     */
    public static final String DANGEROUS_SUBSTANCE_DIRECTIVE = "DangerousSubstancesDirective_lov";
    public static final String DANGEROUS_SUBSTANCE_DIRECTIVE_LOWER = DANGEROUS_SUBSTANCE_DIRECTIVE.toLowerCase();
	
	/**
	 * HAZARDOUS CHEMICAL PRODUCTS
	 */
	public static final String SIGNAL_WORD = "SIGNAL_WORD";
	public static final String SIGNAL_WORD_LOWER = SIGNAL_WORD.toLowerCase();
	public static final String DIS_SUPPHAZARDINFO_TXT = "DIS_SuppHazardInfo_TXT";
	public static final String DIS_SUPPHAZARDINFO_TXT_LOWER = DIS_SUPPHAZARDINFO_TXT.toLowerCase();
	public static final String DIS_HAZARDSTATE_TXT = "DIS_HazardState_TXT";
	public static final String DIS_HAZARDSTATE_TXT_LOWER = DIS_HAZARDSTATE_TXT.toLowerCase();
    /**
     * Energy classes
     */
    public static final String EFFICENCY_FEATURE_LOWER = EFFICENCY_FEATURE.toLowerCase();

    /**
     * Power
     */
    public static final String POWER_FEATURE_LOWER = POWER_FEATURE.toLowerCase();

    /**
     * Energy classes fitting
     */
    public static final String ENERGY_CLASSES_LUMINAR_FITTING_LOWER = ENERGY_CLASSES_LUMINAR_FITTING.toLowerCase();

    /**
     * Energy classes for Bulb
     */
    public static final String ENERGY_CLASSES_LUMINAR_INCLUDED_BULB_LOWER = ENERGY_CLASSES_LUMINAR_INCLUDED_BULB.toLowerCase();

    /**
     * Energy classes for LED.
     */
    public static final String ENERGY_CLASSES_LUMINAR_BUILT_IN_LED_LOWER = ENERGY_CLASSES_LUMINAR_BUILT_IN_LED.toLowerCase();

    /**
     * Create a new instance of {@code Constants}
     */
    private Constants() {
        throw new UnsupportedOperationException("The class " + Constants.class.getName() + " cannot be instanciated!");
    }
}
