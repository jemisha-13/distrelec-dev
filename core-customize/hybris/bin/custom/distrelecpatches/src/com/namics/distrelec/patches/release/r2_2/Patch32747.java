package com.namics.distrelec.patches.release.r2_2;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch32747 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch32747() {
        super("patch_32747", "patch_32747", Release.R2_2, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_32747_010-cms-content-page.impex", languages, updateLanguagesOnly);
    }
}
