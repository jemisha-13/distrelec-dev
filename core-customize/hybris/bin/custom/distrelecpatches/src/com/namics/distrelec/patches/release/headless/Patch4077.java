package com.namics.distrelec.patches.release.headless;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch4077 extends AbstractDemoPatch implements SimpleDemoPatch {
    public Patch4077() {
        super("patch_4077", "patch_4077", Release.HEADLESS, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_4077_010-guest-delivery-countries-biz.impex", languages, updateLanguagesOnly);
    }

}
