package com.namics.distrelec.patches.release.r1_16;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch29352 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch29352() {
        super("patch_29352", "patch_29352", Release.R1_16, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_29352_010_sync-languages.impex", languages, updateLanguagesOnly);
    }
}
