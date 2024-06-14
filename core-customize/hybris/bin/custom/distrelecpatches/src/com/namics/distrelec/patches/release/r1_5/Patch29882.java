package com.namics.distrelec.patches.release.r1_5;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch29882 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch29882() {
        super("patch_29882", "patch_29882", Release.R1_5, StructureState.V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        importGlobalData("rpatch_29882_010-cleanup-cronjob.impex", languages, updateLanguagesOnly);
    }
}
