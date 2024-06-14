/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.patches.release.r16_3;

import static com.namics.distrelec.patches.structure.Release.R16_3;
import static com.namics.distrelec.patches.structure.StructureState.V0;


import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;


import de.hybris.platform.patches.organisation.ImportLanguage;

/**
 *
 */
@Component
public class Patch29055 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch29055() {

        super("patch_29055_1", "patch_29055", R16_3, V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        importGlobalData("rpatch_29055_INT-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_AT-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_BE-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_CH-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_CZ-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_DE-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_DK-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_EE-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_EX-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_FI-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_FR-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_HU-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_IT-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_LT-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_LV-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_NL-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_NO-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_PL-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_RO-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_SE-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_29055_SK-globalDataExample.impex", languages, updateLanguagesOnly);
    }

}