package com.namics.distrelec.patches.release.r1_10;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch29298 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch29298() {
        super("patch_29298", "patch_29298", Release.R1_10, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_29298_010-express-delivery-wording.impex", languages, updateLanguagesOnly);
    }
}
