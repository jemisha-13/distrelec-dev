/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.patches.release.r2_4;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

/**
 *
 */
@Component
public class Patch32422 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch32422() {
        super("patch_32422", "patch_32422", Release.R2_4, StructureState.V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        importGlobalData("rpatch_32422_010-cms-content-page.impex", languages, updateLanguagesOnly);
    }

}
