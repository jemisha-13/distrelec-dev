package com.namics.distrelec.patches.release.r2_1;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch28797 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch28797() {
        super("patch_28797", "patch_28797", Release.R2_1, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_28797_010_add-reconciliation-cronjobs-for-all-sales-orgs.impex", languages, updateLanguagesOnly);
    }
}
