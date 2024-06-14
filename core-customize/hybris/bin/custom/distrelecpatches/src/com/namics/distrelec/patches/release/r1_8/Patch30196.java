package com.namics.distrelec.patches.release.r1_8;

import de.hybris.platform.patches.organisation.ImportLanguage;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;


@Component
public class Patch30196 extends AbstractDemoPatch implements SimpleDemoPatch
{

	public Patch30196()
	{
		super("patch_30196", "patch_30196", Release.R1_8, StructureState.V0);
	}

	@Override
	public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly)
	{
		importGlobalData("rpatch_30196_010-email-logo-update.impex", languages, updateLanguagesOnly);
	}
}
