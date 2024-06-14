package com.namics.distrelec.patches.release.r3_5;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch34189 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch34189() {
        super("patch_34189", "patch_34189", Release.R3_5, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_34189-pimWebUse-migration-job.impex", languages, updateLanguagesOnly);
    }
}