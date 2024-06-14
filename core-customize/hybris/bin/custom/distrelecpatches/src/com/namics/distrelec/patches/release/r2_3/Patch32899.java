package com.namics.distrelec.patches.release.r2_3;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch32899 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch32899() {
        super("patch_32899", "patch_32899", Release.R2_3, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_32899_010-solr-job-retry-config.impex", languages, updateLanguagesOnly);
    }
}
