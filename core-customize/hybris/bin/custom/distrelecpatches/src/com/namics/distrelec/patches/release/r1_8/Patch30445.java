package com.namics.distrelec.patches.release.r1_8;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch30445 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch30445() {
        super("patch_30445_1_8_2", "patch_30445", Release.R1_8, StructureState.V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        importGlobalData("rpatch_30445_010-top-header-slot.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_30445_020-bottom-header-slot.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_30445_030-mobile-header-slot.impex", languages, updateLanguagesOnly);
        executeGroovyScript("rpatch_30445_040-sync-content-catalogs.groovy");
        importGlobalData("rpatch_30445_050-components-for-int-catalog.impex", languages, updateLanguagesOnly);
    }
}
