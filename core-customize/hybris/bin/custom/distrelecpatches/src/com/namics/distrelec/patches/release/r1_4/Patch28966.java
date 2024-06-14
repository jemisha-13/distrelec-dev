package com.namics.distrelec.patches.release.r1_4;

import static com.namics.distrelec.patches.structure.Release.R1_4;
import static com.namics.distrelec.patches.structure.StructureState.V0;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch28966 extends AbstractDemoPatch implements SimpleDemoPatch {
    public Patch28966() {
        super("patch_28966", "patch_28966", R1_4, V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        importGlobalData("rpatch_28966_010-amex-payment.impex", languages, updateLanguagesOnly);
    }
}
