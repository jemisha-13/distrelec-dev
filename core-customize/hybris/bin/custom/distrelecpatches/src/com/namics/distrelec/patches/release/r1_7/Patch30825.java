package com.namics.distrelec.patches.release.r1_7;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch30825 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch30825() {
        super("patch_30825", "patch_30825", Release.R1_7, StructureState.V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        importGlobalData("rpatch_30825_010-fr-site-page-patterns.impex", languages, updateLanguagesOnly);
    }
}
