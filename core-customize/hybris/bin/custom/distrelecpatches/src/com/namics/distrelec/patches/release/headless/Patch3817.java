package com.namics.distrelec.patches.release.headless;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch3817 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch3817() {
        super("patch_3817", "patch_3817", Release.HEADLESS, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_3817_010-cms-content-headless.impex", languages, updateLanguagesOnly);
    }
}
