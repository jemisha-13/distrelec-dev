package com.namics.distrelec.patches.release.r2_5;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch33187 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch33187() {
        super("patch_33187_2", "patch_33187", Release.R2_5, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_33187_010-remove-ymkt-cronjobs.impex", languages, updateLanguagesOnly);
    }
}
