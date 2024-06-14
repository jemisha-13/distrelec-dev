package com.namics.distrelec.patches.release.r1_1;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch4749 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch4749() {
        super("patch_4749_1", "patch_4749", Release.R1_1, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_4749_010-compare_pages.impex", languages, updateLanguagesOnly);
    }
}
