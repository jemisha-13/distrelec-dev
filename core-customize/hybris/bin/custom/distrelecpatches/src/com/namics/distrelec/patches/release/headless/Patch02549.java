package com.namics.distrelec.patches.release.headless;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch02549 extends AbstractDemoPatch implements SimpleDemoPatch {
    public Patch02549() {
        super("patch_02549", "patch_02549", Release.HEADLESS, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_02549_010-cms-bom-tool-links.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_02549_020-right-component.impex", languages, updateLanguagesOnly);
    }
}
