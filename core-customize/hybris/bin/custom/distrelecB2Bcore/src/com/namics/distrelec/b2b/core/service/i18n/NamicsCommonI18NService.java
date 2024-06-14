/**
 * 
 */
package com.namics.distrelec.b2b.core.service.i18n;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.c2l.RegionModel;

/**
 * NamicsCommonI18NService for country and region session support.
 * 
 * @author rhusi
 * @since Namics Extensions 1.0
 * 
 * @spring.bean namicsCommonI18NService
 * 
 */
public interface NamicsCommonI18NService {

    /**
     * Gets current session country.
     * 
     * @return current session country or null if not set
     */
    CountryModel getCurrentCountry();

    /**
     * Sets the current country to the session.
     * 
     * @param country
     *            the new country
     */
    void setCurrentCountry(final CountryModel country);

    /**
     * Gets current session region.
     * 
     * @return current session region or null if not set
     */
    RegionModel getCurrentRegion();

    /**
     * Sets the current region to the session.
     * 
     * @param region
     *            the new region
     */
    void setCurrentRegion(final RegionModel region);

    /**
     * Get the Language for a given 3 character isocode form pim
     * 
     * @param isocodePim
     * @return the language with the given pim isocode
     */
    LanguageModel getLanguageModelByIsocodePim(final String isocodePim);
    
    LanguageModel getLanguageModelByIsocode(final String isocode);
}
