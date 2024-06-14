package com.namics.distrelec.patches.release.r1_1;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch4906 extends AbstractDemoPatch implements SimpleDemoPatch {
    public Patch4906() {
        super("patch_4906", "patch_4906", Release.R1_1, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_4906_010-add-missing-mineral-pdf.impex", languages, updateLanguagesOnly);
    }
}
