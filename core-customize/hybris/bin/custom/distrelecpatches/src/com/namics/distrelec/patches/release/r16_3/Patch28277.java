package com.namics.distrelec.patches.release.r16_3;

import java.util.Set;

import org.springframework.stereotype.Component;

import static com.namics.distrelec.patches.structure.Release.R16_3;
import static com.namics.distrelec.patches.structure.StructureState.V0;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch28277 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch28277() {
        super("patch_28277", "patch_28277", R16_3, V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_28277_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
