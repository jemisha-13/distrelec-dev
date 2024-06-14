package com.namics.distrelec.patches.release.r1_5;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch30447 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch30447() {
        super("patch_30447", "patch_30447", Release.R1_5, StructureState.V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        importGlobalData("rpatch_30447_010-update_MainTopNavNode.impex", languages, updateLanguagesOnly);
    }
}
