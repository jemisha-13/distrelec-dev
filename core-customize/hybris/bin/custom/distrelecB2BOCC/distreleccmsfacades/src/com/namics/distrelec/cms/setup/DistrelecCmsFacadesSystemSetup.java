/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.cms.setup;

import static com.namics.distrelec.cms.constants.DistreleccmsfacadesConstants.PLATFORM_LOGO_CODE;

import com.namics.distrelec.cms.service.DistrelecCmsFacadesService;
import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.namics.distrelec.cms.constants.DistreleccmsfacadesConstants;


@SystemSetup(extension = DistreleccmsfacadesConstants.EXTENSIONNAME)
public class DistrelecCmsFacadesSystemSetup {
    private final DistrelecCmsFacadesService distrelecCmsFacadesService;

    public DistrelecCmsFacadesSystemSetup(final DistrelecCmsFacadesService distrelecCmsFacadesService) {
        this.distrelecCmsFacadesService = distrelecCmsFacadesService;
    }

    @SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
    public void createEssentialData() {
        distrelecCmsFacadesService.createLogo(PLATFORM_LOGO_CODE);
    }

    private InputStream getImageStream() {
        return DistrelecCmsFacadesSystemSetup.class.getResourceAsStream("/distreleccmsfacades/sap-hybris-platform.png");
    }
}
