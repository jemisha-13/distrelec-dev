package com.namics.distrelec.patches.release.r1_4;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch28969 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch28969() {
        super("patch_28969", "patch_28969", Release.R1_4, StructureState.V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        importGlobalData("rpatch_28969_010-amex-logo-main-footer.impex", languages, updateLanguagesOnly);
    }
}
