/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.patches.release.r3_6;

import static com.namics.distrelec.patches.structure.Release.R3_6;
import static com.namics.distrelec.patches.structure.StructureState.V0;

import de.hybris.platform.patches.Rerunnable;
import de.hybris.platform.patches.organisation.ImportLanguage;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;


/**
 *
 */
@Component
public class Patch29786 extends AbstractDemoPatch implements SimpleDemoPatch, Rerunnable
{

	public Patch29786()
	{
		super("patch_29786_12", "patch_29786", R3_6, V0);
	}

	@Override
	public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly)
	{
		importGlobalData("rpatch_29786_010-cms-content-page.impex", languages, updateLanguagesOnly);
	}

}

