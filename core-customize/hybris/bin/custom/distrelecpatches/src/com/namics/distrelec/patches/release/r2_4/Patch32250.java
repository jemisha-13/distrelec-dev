package com.namics.distrelec.patches.release.r2_4;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch32250 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch32250() {
        super("patch_32250_3", "patch_32250", Release.R2_4, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_32250_010-cms-sap-recommendation.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_32250_020-remove-attribute-descriptor.impex", languages, updateLanguagesOnly);
        executeGroovyScript("rpatch_32250_030-alter-table-cmscomponent.groovy");
    }
}
