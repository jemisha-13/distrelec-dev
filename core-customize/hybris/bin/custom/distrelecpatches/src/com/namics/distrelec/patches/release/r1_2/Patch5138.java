package com.namics.distrelec.patches.release.r1_2;

import de.hybris.platform.patches.organisation.ImportLanguage;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;


@Component
public class Patch5138 extends AbstractDemoPatch implements SimpleDemoPatch
{

	public Patch5138()
	{
		super("patch_5138", "patch_5138", Release.R1_2, StructureState.V0);
	}

	@Override
	public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly)
	{
		importGlobalData("rpatch_5138_010-cmrt-compliance-pdf.impex", languages, updateLanguagesOnly);
	}
}
