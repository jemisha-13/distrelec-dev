package com.namics.distrelec.patches.release.r1_12;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch28432 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch28432() {
        super("patch_28432", "patch_28432", Release.R1_12, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_28432_010-change-fr-translation.impex", languages, updateLanguagesOnly);
    }
}
