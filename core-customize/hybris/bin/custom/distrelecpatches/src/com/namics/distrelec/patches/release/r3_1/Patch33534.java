/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.patches.release.r3_1;

import static com.namics.distrelec.patches.structure.Release.R3_1;
import static com.namics.distrelec.patches.structure.StructureState.V0;

import de.hybris.platform.patches.organisation.ImportLanguage;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;


/**
 *
 */
@Component
public class Patch33534 extends AbstractDemoPatch implements SimpleDemoPatch
{

	public Patch33534()
	{
		super("patch_33534_3", "patch_33534", R3_1, V0);
	}

	@Override
	public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly)
	{
		importGlobalData("rpatch_33534_010-cms-content-page.impex", languages, updateLanguagesOnly);
	}
}
