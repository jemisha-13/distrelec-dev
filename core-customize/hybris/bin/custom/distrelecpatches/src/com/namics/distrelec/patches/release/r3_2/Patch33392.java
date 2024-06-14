package com.namics.distrelec.patches.release.r3_2;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch33392 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch33392() {
        super("patch_33392", "patch_33392", Release.R3_2, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        executeGroovyScript("rpatch_33392-update-consent-condition-required-flag.groovy");
    }
}
