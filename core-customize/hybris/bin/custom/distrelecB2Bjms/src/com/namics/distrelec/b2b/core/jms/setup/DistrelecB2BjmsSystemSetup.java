/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.namics.distrelec.b2b.core.jms.setup;

import static com.namics.distrelec.b2b.core.jms.constants.DistrelecB2BjmsConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.namics.distrelec.b2b.core.jms.constants.DistrelecB2BjmsConstants;
import com.namics.distrelec.b2b.core.jms.service.DistrelecB2BjmsService;

@SystemSetup(extension = DistrelecB2BjmsConstants.EXTENSIONNAME)
public class DistrelecB2BjmsSystemSetup {
    private final DistrelecB2BjmsService distrelecB2BjmsService;

    public DistrelecB2BjmsSystemSetup(final DistrelecB2BjmsService distrelecB2BjmsService) {
        this.distrelecB2BjmsService = distrelecB2BjmsService;
    }

    @SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
    public void createEssentialData() {
        distrelecB2BjmsService.createLogo(PLATFORM_LOGO_CODE);
    }

    private InputStream getImageStream() {
        return DistrelecB2BjmsSystemSetup.class.getResourceAsStream("/distrelecB2Bjms/sap-hybris-platform.png");
    }
}
