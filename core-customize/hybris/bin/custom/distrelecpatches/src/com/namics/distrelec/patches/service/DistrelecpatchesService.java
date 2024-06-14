/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.patches.service;

public interface DistrelecpatchesService {
    String getHybrisLogoUrl(String logoCode);

    void createLogo(String logoCode);
}
