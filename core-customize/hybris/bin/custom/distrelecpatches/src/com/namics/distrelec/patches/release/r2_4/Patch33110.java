package com.namics.distrelec.patches.release.r2_4;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch33110 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch33110() {
        super("patch_33110", "patch_33110", Release.R2_4, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_33110_010-dist-footer-component.impex", languages, updateLanguagesOnly);
    }
}
