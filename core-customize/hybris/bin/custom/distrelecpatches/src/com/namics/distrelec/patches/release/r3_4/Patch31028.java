/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.patches.release.r3_4;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.Rerunnable;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;


/**
 *
 */
@Component
public class Patch31028 extends AbstractDemoPatch implements SimpleDemoPatch, Rerunnable
{

	public Patch31028()
	{
		super("patch_31028_8", "patch_31028", Release.R3_4, StructureState.V0);
	}


	@Override
	public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly)
	{
		this.
		executeGroovyScript("cleanup_itemsynctimestamps.groovy");
		executeGroovyScript("cleanup_abstractbannercomponent.groovy");
		executeGroovyScript("cleanup_distcarpetitem.groovy");
		executeGroovyScript("cleanup_distextcarpetitem.groovy");
		executeGroovyScript("cleanup_distcarpetcomponent.groovy");
		executeGroovyScript("cleanup_distextcarpetcomponent.groovy");
		executeGroovyScript("cleanup_distherorotatingteaseritem.groovy");
		executeGroovyScript("cleanup_distherototatingteasercomponent.groovy");
		executeGroovyScript("cleanup_distreleccategorygriditemcomponent.groovy");
		executeGroovyScript("cleanup_distcarpetcontentteaser.groovy");
		executeGroovyScript("cleanup_distherorotatingteaser.groovy");
		executeGroovyScript("cleanup_distproductboxcomponent.groovy");
		executeGroovyScript("cleanup_cmstimerestriction.groovy");
		executeGroovyScript("cleanup_cmsnavigationentry.groovy");
		executeGroovyScript("cleanup_contentslotforpage.groovy");
		executeGroovyScript("cleanup_contentslotfortemplate.groovy");
		importGlobalData("rpatch_31028-catalog-cleanup.impex", languages, updateLanguagesOnly);
	}

}

