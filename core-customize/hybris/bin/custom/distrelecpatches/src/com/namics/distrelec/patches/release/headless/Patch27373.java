package com.namics.distrelec.patches.release.headless;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;

import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class Patch27373 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch27373() {
        super("patch_027373_1", "patch_027373", Release.HEADLESS, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_27373_010-cms-content-headless.impex", languages, updateLanguagesOnly);
    }
}
