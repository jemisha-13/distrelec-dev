package com.namics.distrelec.patches.release.r1_2;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch5197 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch5197() {
        super("patch_5197", "patch_5197", Release.R1_2, StructureState.V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        importGlobalData("rpatch_5197_010-component-recursion.impex", languages, updateLanguagesOnly);
    }
}
