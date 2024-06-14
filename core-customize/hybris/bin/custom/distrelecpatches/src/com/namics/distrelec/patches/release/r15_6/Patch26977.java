package com.namics.distrelec.patches.release.r15_6;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch26977 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch26977() {
        super("patch_26977_2", "patch_26977", Release.R15_6, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_26977_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
