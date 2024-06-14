/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.patches.release.headless;

import de.hybris.platform.patches.organisation.ImportLanguage;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;


@Component
public class Patch4164 extends AbstractDemoPatch implements SimpleDemoPatch
{
	public Patch4164()
	{
		super("patch_4164", "patch_4164", Release.HEADLESS, StructureState.V0);
	}

	@Override
	public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly)
	{
		importGlobalData("rpatch_4164_010-cms-content-headless.impex", languages, updateLanguagesOnly);
	}
}

