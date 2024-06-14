package com.namics.distrelec.patches.release.r16_3;

import static com.namics.distrelec.patches.structure.Release.R16_3;
import static com.namics.distrelec.patches.structure.StructureState.V0;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch25177 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch25177() {
        super("patch_25177", "patch_25177", R16_3, V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_25177_010-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_25177_020-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_25177_030-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_25177_040-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_25177_050-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_25177_060-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_25177_070-globalDataExample.impex", languages, updateLanguagesOnly);
    }

}
