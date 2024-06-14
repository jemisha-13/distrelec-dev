package com.namics.distrelec.patches.release.r3_4;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch34406 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch34406() {
        super("patch_34406", "patch_34406", Release.R3_4, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_34406-fix-fusion-update-query.impex", languages, updateLanguagesOnly);
    }

}
