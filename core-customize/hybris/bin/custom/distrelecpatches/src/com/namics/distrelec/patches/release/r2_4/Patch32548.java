package com.namics.distrelec.patches.release.r2_4;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch32548 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch32548() {
        super("patch_32548", "patch_32548", Release.R2_4, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_32548_010-ch-tax-job.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_32548_020-reset-price-ch-job.impex", languages, updateLanguagesOnly);
    }
}
