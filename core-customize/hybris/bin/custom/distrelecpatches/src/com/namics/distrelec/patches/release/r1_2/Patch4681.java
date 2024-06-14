package com.namics.distrelec.patches.release.r1_2;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch4681 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch4681() {
        super("patch_4681", "patch_4681", Release.R1_2, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_4681_010-services-links-at-be.impex", languages, updateLanguagesOnly);
    }
}
