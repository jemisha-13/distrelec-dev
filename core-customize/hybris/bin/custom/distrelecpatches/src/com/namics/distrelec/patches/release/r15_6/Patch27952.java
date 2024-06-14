package com.namics.distrelec.patches.release.r15_6;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch27952 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch27952() {
        super("patch_27952", "patch_27952", Release.R15_6, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_27952_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
