package com.namics.distrelec.patches.release.r16_1;

import static com.namics.distrelec.patches.structure.Release.R16_1;
import static com.namics.distrelec.patches.structure.StructureState.V0;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch28705 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch28705() {
        super("patch_28705", "patch_28705", R16_1, V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_28705_AT-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_BE-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_CH-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_CZ-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_DE-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_DK-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_EE-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_EX-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_FI-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_FR-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_HU-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_INT-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_IT-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_LT-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_LV-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_NL-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_NO-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_PL-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_RO-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_SE-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28705_SK-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
