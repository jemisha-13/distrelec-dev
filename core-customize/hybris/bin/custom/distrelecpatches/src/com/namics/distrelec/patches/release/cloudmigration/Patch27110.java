package com.namics.distrelec.patches.release.cloudmigration;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch27110 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch27110() {
        super("patch_27110", "patch_27110", Release.CCV2, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_27110_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}