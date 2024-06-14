package com.namics.distrelec.patches.release.r2_4;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch32517 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch32517() {
        super("patch_32517", "patch_32517", Release.R2_4, com.namics.distrelec.patches.structure.StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_32517_010_add-email-template.impex", languages, updateLanguagesOnly);
    }
}
