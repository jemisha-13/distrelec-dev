/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.patches.release.headless;

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
public class Patch3400 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch3400() {
        super("patch_3400", "patch_3400", Release.HEADLESS, StructureState.V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        importGlobalData("rpatch_3400_010-all-manufacturer-url.impex", languages, updateLanguagesOnly);
    }
}
