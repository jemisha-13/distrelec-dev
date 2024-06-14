package com.namics.distrelec.patches.release.r15_1;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch26603 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch26603() {
        super("patch_26603", "patch_26603", Release.R15_1, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_26603_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
