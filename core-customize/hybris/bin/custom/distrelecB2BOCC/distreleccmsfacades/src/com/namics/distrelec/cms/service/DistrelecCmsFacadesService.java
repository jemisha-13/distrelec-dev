/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.cms.service;

/**
 * Extending the hybris DistreleccmsfacadesService.
 *
 * @author mahesh, Distrelec
 */
public interface DistrelecCmsFacadesService {

    /**
     * Returns the logo url from media
     *
     * @param logoCode code (uuid) of logo
     * @return the url of requested media.
     */
    String getHybrisLogoUrl(String logoCode);

    /**
     * create the logo and set stream for media
     *
     * @param logoCode code (uuid) of logo
     * @void search for existing logo. If existing logo is not present it creates and sets Stream For Media in MediaService.
     */
    void createLogo(String logoCode);
}
