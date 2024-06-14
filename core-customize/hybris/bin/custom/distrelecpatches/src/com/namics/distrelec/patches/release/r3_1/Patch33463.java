/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.patches.release.r3_1;

import de.hybris.platform.patches.organisation.ImportLanguage;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;


@Component
public class Patch33463 extends AbstractDemoPatch implements SimpleDemoPatch
{

	public Patch33463()
	{
		super("patch_33463", "patch_33463", Release.R3_1, StructureState.V0);
	}


	@Override
	public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly)
	{
		importGlobalData("rpatch_33463_010-content-page-mapping.impex", languages, updateLanguagesOnly);
	}
}
