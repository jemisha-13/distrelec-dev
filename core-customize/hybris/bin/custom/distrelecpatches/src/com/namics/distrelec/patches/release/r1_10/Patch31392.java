package com.namics.distrelec.patches.release.r1_10;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch31392 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch31392() {
        super("patch_31392", "patch_31392", Release.R1_10, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_31392_010-import-rs-logo-images.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_31392_020-update-logo-component-with-rs-images.impex", languages, updateLanguagesOnly);
    }
}
