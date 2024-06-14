package com.namics.distrelec.patches.release.headless;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;

import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class Patch26291 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch26291() {
        super("patch_26291_7", "patch_26291", Release.HEADLESS, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_26291_010-breadcrumbs.impex", languages, updateLanguagesOnly);
    }
}
