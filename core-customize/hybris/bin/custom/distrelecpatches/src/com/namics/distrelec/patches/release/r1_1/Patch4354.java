package com.namics.distrelec.patches.release.r1_1;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch4354 extends AbstractDemoPatch implements SimpleDemoPatch {
    public Patch4354() {
        super("patch_4354_2", "patch_4354", Release.R1_1, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_4354_010-cms-content-checkout.impex", languages, updateLanguagesOnly);
    }
}
