package com.namics.distrelec.patches.release.fusion2;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch33776 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch33776() {
        super("patch_33776_2", "patch_33776", Release.FUSION_2, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_33776_010_activate-full-dk-jobs.impex", languages, updateLanguagesOnly);
    }
}
