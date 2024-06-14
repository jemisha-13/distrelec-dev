package com.namics.distrelec.patches.release.r16_3;

import static com.namics.distrelec.patches.structure.Release.R16_3;
import static com.namics.distrelec.patches.structure.StructureState.V0;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch28800 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch28800() {
        super("patch_28800", "patch_28800", R16_3, V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        
        importGlobalData("rpatch_28800_010-fusionSetup.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_28800_010-productStatusTranslation.impex", languages, updateLanguagesOnly);
    }
}
