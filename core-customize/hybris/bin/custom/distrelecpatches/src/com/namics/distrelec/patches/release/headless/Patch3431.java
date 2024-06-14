/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.patches.release.headless;

import de.hybris.platform.patches.organisation.ImportLanguage;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;


/**
 *
 */
@Component
public class Patch3431 extends AbstractDemoPatch implements SimpleDemoPatch
{

    public Patch3431()
    {
        super("patch_3431", "patch_3431", Release.HEADLESS, StructureState.V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly)
    {
        importGlobalData("rpatch_3431_010-cms-pagelabels.impex", languages, updateLanguagesOnly);
    }
}

