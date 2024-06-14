package com.namics.distrelec.patches.release.r3_1;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch33137 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch33137() {
        super("patch_33137_2", "patch_33137", Release.R3_1, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_33137_010-legal-address-change.impex", languages, updateLanguagesOnly);
    }
}
