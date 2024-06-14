package com.namics.distrelec.patches.release.r2_2;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch32448 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch32448() {
        super("patch_32448_4", "patch_32448", Release.R3_2, StructureState.V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        importGlobalData("rpatch_32448-eol-sales-status-flag.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_32448-reconfigure-cronjob.impex", languages, updateLanguagesOnly);
        executeGroovyScript("updateEolTimestamp.groovy");
    }
}
