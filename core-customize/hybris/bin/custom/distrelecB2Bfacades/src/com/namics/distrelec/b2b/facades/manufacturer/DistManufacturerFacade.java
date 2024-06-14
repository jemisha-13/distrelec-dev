/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.manufacturer;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerMenuData;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistMiniManufacturerData;
import com.namics.distrelec.b2b.facades.smartedit.SmarteditManufacturerData;
import de.hybris.platform.cms2.model.site.CMSSiteModel;

import java.util.List;
import java.util.Map;

/**
 * Manufacturer facade interface.
 *
 * @author pbueschi, Namics AG
 */
public interface DistManufacturerFacade {

    /**
     * Gets alphabetic sorted map of all manufacturers. The DTO's are just filled with the code, name and urlId.
     *
     * @return Map<String, List < DistMiniManufacturerData>>
     */
    Map<String, List<DistMiniManufacturerData>> getManufactures();

    List<CMSSiteModel> getAvailableCMSSitesForManufacturer(DistManufacturerModel manufacturer);

    /**
     * Search manufacturers by code or name
     *
     * @param term
     * @param page
     * @param pageSize
     * @return
     */
    List<SmarteditManufacturerData> searchManufacturers(String term, int page, int pageSize);

    /**
     * Find single manufacturer by code
     * @param code
     * @return
     */
    SmarteditManufacturerData findManufacturerByCode(String code);


    /**
     * Gets alphabetic sorted map of all manufacturers. The DTO's are just filled with the code, name and urlId.
     * @return  Map<String, List<DistMiniManufacturerData>>
     */
    List<DistManufacturerMenuData> getManufacturesForOCC();

    boolean isManufacturerExcluded(String manufacturerCode);

}
