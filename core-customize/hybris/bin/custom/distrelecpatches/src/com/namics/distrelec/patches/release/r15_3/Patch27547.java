package com.namics.distrelec.patches.release.r15_3;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch27547 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch27547() {
        super("patch_27547", "patch_27547", Release.R15_3, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_27547_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
