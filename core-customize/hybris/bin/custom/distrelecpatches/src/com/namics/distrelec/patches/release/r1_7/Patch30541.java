package com.namics.distrelec.patches.release.r1_7;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch30541 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch30541() {
        super("patch_30541", "patch_30541", Release.R1_7, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_30541_010-quality-and-legal-reports-link-translation.impex", languages, updateLanguagesOnly);
    }
}
