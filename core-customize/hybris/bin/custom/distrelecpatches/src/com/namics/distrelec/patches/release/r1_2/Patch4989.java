package com.namics.distrelec.patches.release.r1_2;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch4989 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch4989() {
        super("patch_4989", "patch_4989", Release.R1_2, StructureState.V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        importGlobalData("rpatch_4989_010-mineral-docs-staging.impex", languages, updateLanguagesOnly);
    }
}
