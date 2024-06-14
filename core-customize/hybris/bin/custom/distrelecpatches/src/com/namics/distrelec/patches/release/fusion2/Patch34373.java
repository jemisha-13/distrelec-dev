package com.namics.distrelec.patches.release.fusion2;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch34373 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch34373() {
        super("patch_34373_2", "patch_34373", Release.FUSION_2, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_34373_010_fusion_country_mv.impex", languages, updateLanguagesOnly);
    }
}