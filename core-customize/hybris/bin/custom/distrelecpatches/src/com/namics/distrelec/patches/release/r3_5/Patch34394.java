package com.namics.distrelec.patches.release.r3_5;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch34394 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch34394() {
        super("patch_34394", "patch_34394", Release.R3_5, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_34394_010-enable-sub-user-cms-site.impex", languages, updateLanguagesOnly);
    }
}
