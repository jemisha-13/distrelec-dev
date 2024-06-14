package com.namics.distrelec.patches.release.r15_7;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch28434 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch28434() {
        super("patch_28434", "patch_28434", Release.R15_8, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_28434_010-orderCancellation.impex", languages, updateLanguagesOnly);
    }
}
