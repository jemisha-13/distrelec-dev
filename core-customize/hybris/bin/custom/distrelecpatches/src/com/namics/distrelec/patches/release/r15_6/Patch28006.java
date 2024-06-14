package com.namics.distrelec.patches.release.r15_6;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch28006 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch28006() {
        super("patch_28006", "patch_28006", Release.R15_6, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_28006_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
