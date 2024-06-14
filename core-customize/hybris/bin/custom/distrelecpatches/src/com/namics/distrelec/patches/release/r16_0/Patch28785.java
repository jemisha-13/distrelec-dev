package com.namics.distrelec.patches.release.r16_0;

import static com.namics.distrelec.patches.structure.Release.R16_0;
import static com.namics.distrelec.patches.structure.StructureState.V0;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch28785 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch28785() {
        super("patch_28785", "patch_28785", R16_0, V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_28785_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
