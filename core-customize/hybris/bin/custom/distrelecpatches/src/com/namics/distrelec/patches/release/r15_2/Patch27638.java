package com.namics.distrelec.patches.release.r15_2;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch27638 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch27638() {
        super("patch_27638", "patch_27638", Release.R15_2, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_27638_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
