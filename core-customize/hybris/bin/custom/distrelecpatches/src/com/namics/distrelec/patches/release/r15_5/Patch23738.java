package com.namics.distrelec.patches.release.r15_5;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch23738 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch23738() {
        super("patch_23738", "patch_23738", Release.R15_5, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_23738_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
