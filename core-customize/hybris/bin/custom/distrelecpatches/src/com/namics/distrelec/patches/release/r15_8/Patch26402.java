package com.namics.distrelec.patches.release.r15_8;

import java.util.Set;

import org.springframework.stereotype.Component;

import static com.namics.distrelec.patches.structure.Release.R15_8;
import static com.namics.distrelec.patches.structure.StructureState.V0;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch26402 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch26402() {
        super("patch_26402", "patch_26402", R15_8, V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_26402_010-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_26402_020-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_26402_030-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_26402_040-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_26402_050-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_26402_060-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
