/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.patches.release.r2_6;

import static com.namics.distrelec.patches.structure.Release.R2_6;
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
public class Patch30897 extends AbstractDemoPatch implements SimpleDemoPatch
{

	public Patch30897()
	{
		super("patch_30897_1", "patch_30897", R2_6, V0);
	}

	@Override
	public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly)
	{
		importGlobalData("rpatch_30897_010-globalDataExample.impex", languages, updateLanguagesOnly);
	}
}
