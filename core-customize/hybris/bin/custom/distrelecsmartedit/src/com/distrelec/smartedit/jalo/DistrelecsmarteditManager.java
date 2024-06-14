/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.distrelec.smartedit.jalo;

import com.distrelec.smartedit.constants.DistrelecsmarteditConstants;
import org.apache.log4j.Logger;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;

@SuppressWarnings("PMD")
public class DistrelecsmarteditManager extends GeneratedDistrelecsmarteditManager {
    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(DistrelecsmarteditManager.class.getName());

    public static final DistrelecsmarteditManager getInstance() {
        ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
        return (DistrelecsmarteditManager) em.getExtension(DistrelecsmarteditConstants.EXTENSIONNAME);
    }

}
