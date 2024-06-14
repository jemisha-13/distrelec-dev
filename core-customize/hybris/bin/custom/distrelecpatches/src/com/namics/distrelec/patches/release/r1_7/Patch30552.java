package com.namics.distrelec.patches.release.r1_7;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch30552 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch30552() {
        super("patch_30552_1_7_5", "patch_30552", Release.R1_7, StructureState.V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        importGlobalData("rpatch_30552_010-manufacturer-navigation.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_30552_020-add-missing-entries.impex", languages, updateLanguagesOnly);
        executeGroovyScript("rpatch_30552_030-sync-content-catalogs.groovy");
        importGlobalData("rpatch_30552_040-manufacturer-nav-int-catalog.impex", languages, updateLanguagesOnly);
    }
}
