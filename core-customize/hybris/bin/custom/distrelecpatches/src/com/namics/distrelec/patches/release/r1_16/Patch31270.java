package com.namics.distrelec.patches.release.r1_16;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch31270 extends AbstractDemoPatch implements SimpleDemoPatch {
    public Patch31270() {
        super("patch_31270", "patch_31270", Release.R1_16, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_31270_010-newsletter-success.impex", languages, updateLanguagesOnly);
    }

}
