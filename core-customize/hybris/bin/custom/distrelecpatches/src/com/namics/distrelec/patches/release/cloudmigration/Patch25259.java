package com.namics.distrelec.patches.release.cloudmigration;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch25259 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch25259() {
        super("patch_25259", "patch_25259", Release.CCV2, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_25259_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
