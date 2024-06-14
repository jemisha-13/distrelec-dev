package com.namics.distrelec.patches.release.r15_7;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch28435 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch28435() {
        super("patch_28435", "patch_28435", Release.R15_7, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_28435_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
