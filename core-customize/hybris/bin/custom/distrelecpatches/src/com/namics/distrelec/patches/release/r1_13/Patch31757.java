package com.namics.distrelec.patches.release.r1_13;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch31757 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch31757() {
        super("patch_31757_2", "patch_31757", Release.R1_13, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_31757_010-update-deliveryMode.impex", languages, updateLanguagesOnly);
    }
}
