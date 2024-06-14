package com.namics.distrelec.patches.release.r15_5;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch27808 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch27808() {
        super("patch_27808_3", "patch_27808", Release.R15_5, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_27808_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
