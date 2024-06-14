package com.namics.distrelec.patches.release.r2_1;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch31340 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch31340() {
        super("patch_31340", "patch_31340", Release.R2_1, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_31340_010-content-page-mapping.impex", languages, updateLanguagesOnly);
    }
}
