package com.namics.distrelec.patches.release.r2_4;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch32887 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch32887() {
        super("patch_32887_1", "patch_32887", Release.R2_4, com.namics.distrelec.patches.structure.StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_32887-rs-oauth.impex", languages, updateLanguagesOnly);
    }
}
