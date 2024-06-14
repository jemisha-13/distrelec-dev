package com.namics.distrelec.patches.release.headless;

import de.hybris.platform.patches.organisation.ImportLanguage;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;


@Component
public class Patch4443 extends AbstractDemoPatch implements SimpleDemoPatch
{

	public Patch4443()
	{
		super("patch_4443", "patch_4443", Release.HEADLESS, StructureState.V0);
	}

	@Override
	public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly)
	{
		importGlobalData("rpatch_4443_010-pl-footerlink-jobs.impex", languages, updateLanguagesOnly);

	}
}
