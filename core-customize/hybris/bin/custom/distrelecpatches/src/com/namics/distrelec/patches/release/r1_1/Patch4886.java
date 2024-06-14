package com.namics.distrelec.patches.release.r1_1;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch4886 extends AbstractDemoPatch implements SimpleDemoPatch {
    public Patch4886() {
        super("patch_4886", "patch_4886", Release.R1_1, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_4886_010-order-confirmation-component.impex", languages, updateLanguagesOnly);
    }
}
