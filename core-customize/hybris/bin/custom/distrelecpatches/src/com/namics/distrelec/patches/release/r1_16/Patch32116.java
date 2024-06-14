package com.namics.distrelec.patches.release.r1_16;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch32116 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch32116() {
        super("patch_32116", "patch_32116", Release.R1_16, StructureState.V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        executeGroovyScript("resetSalesStatusLastModifiedTime.groovy");
    }
}
