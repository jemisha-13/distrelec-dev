package com.namics.distrelec.patches.release.upgrade2211;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch31041 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch31041() {
        super("patch_31041_3", "patch_31041", Release.UPGRADE_2211, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
         importGlobalData("rpatch_31041_010-cmssite-preview-url.impex", languages, updateLanguagesOnly);
         executeGroovyScript("fix-page-name.groovy");
    }
}
