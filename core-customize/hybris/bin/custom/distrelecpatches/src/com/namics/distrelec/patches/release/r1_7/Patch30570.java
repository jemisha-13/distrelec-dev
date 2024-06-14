package com.namics.distrelec.patches.release.r1_7;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch30570 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch30570() {
        super("patch_30570", "patch_30570", Release.R1_7, StructureState.V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        importGlobalData("rpatch_30570_010-breadcrumb-translations.impex", languages, updateLanguagesOnly);
    }
}
