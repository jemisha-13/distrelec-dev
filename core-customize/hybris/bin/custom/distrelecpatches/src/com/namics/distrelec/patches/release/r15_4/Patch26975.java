package com.namics.distrelec.patches.release.r15_4;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch26975 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch26975() {
        super("patch_26975", "patch_26975", Release.R15_4, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_26975_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
