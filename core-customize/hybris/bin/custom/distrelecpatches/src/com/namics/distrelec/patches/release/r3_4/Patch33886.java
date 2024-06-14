package com.namics.distrelec.patches.release.r3_4;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch33886 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch33886() {
        super("patch_33886", "patch_33886", Release.R3_4, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_33886-smartedit-preview-category.impex", languages, updateLanguagesOnly);
    }
}
