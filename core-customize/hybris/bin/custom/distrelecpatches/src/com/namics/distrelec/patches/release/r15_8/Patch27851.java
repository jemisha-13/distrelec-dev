package com.namics.distrelec.patches.release.r15_8;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch27851 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch27851() {
        super("patch_27851_4", "patch_27851", Release.R15_8, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_27851_AT-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_BE-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_CH-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_CZ-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_DE-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_DK-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_EE-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_EX-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_FI-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_FR-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_HU-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_INT-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_IT-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_LT-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_LV-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_NL-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_NO-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_PL-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_RO-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_SE-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27851_SK-globalDataExample.impex", languages, updateLanguagesOnly);
        executeGroovyScript("update-robots-tags.groovy");
        executeGroovyScript("sync-catalogs.groovy");
    }
}
