package com.namics.distrelec.patches.release.r16_1;

import static com.namics.distrelec.patches.structure.Release.R16_1;
import static com.namics.distrelec.patches.structure.StructureState.V0;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch28228 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch28228() {
        super("patch_28228", "patch_28228", R16_1, V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_28228_010-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28228_020-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
