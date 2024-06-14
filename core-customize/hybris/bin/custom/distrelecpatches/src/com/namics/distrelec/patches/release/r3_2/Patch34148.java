package com.namics.distrelec.patches.release.r3_2;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch34148 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch34148() {
        super("patch_34148", "patch_34148", Release.R3_2, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_34148-update-ff-job.impex", languages, updateLanguagesOnly);
    }
}
