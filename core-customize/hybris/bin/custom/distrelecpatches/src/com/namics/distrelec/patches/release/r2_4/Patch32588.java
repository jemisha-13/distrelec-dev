package com.namics.distrelec.patches.release.r2_4;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch32588 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch32588() {
        super("patch_32588", "patch_32588", Release.R2_4, com.namics.distrelec.patches.structure.StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_32588-user-migration-script.impex", languages, updateLanguagesOnly);
    }
}
