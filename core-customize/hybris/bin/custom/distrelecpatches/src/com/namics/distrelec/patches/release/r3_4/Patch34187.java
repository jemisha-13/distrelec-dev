package com.namics.distrelec.patches.release.r3_4;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch34187 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch34187() {
        super("patch_34187", "patch_34187", Release.R3_4, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_34187-increase-index-rate.impex", languages, updateLanguagesOnly);
    }
}