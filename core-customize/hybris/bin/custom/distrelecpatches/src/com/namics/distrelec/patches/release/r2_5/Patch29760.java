/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.patches.release.r2_4;

import de.hybris.platform.patches.organisation.ImportLanguage;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.constants.CatalogConstants;
import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;


/**
 *
 */
@Component
public class Patch29760 extends AbstractDemoPatch implements SimpleDemoPatch
{

	public Patch29760()
	{
		super("patch_29760_5", "patch_29760", Release.R2_5, StructureState.V0);
	}

	@Override
	public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly)
	{
		importGlobalData("rpatch_29760-meta-update.impex", languages, updateLanguagesOnly);
		registerCatalogsForSynchronization(CatalogConstants.ALL_CONTENT_CONTENTCATALOGS);
	}
}
