package com.namics.distrelec.patches.release.r2_2;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch_31203 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch_31203() {
        super("patch_31203_3", "patch_31203", Release.R2_2, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_32481_010_remove-old-jobs.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_31537_020_base-config.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_31537_030_units-job.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_31537_040_classification-attribute-assignment-job.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_30974_050_punchout.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_31537_060_full-update-prodCatMan.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_31537_070_full-eol-products.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_31537_080_update-atomic-product.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_32481_090_composite-job-structure.impex", languages, updateLanguagesOnly);

        // manually execute on environment to set nodeGroup and activate schedule
        // DISTRELEC-32481-cron-job-node-group.impex
        // DISTRELEC-32481-activate-cron-job-triggers.impex
    }
}
