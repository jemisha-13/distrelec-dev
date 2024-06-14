package com.namics.distrelec.patches.release.fusion2;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch33614 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch33614() {
        super("patch_33614_2", "patch_33614", Release.FUSION_2, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_33614_010_activate-full-biz-jobs.impex", languages, updateLanguagesOnly);
    }
}
