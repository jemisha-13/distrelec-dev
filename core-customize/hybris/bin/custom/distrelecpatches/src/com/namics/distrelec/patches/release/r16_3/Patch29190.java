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
public class Patch29190 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch29190() {
        super("patch_29190", "patch_29190", R16_3, V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        importGlobalData("rpatch_29190_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
