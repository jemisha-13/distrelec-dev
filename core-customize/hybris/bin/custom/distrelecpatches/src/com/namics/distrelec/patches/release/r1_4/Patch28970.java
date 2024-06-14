package com.namics.distrelec.patches.release.r1_4;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.namics.distrelec.patches.structure.Release.R1_4;
import static com.namics.distrelec.patches.structure.StructureState.V0;

@Component
public class Patch28970 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch28970() {
        super("patch_28970", "patch_28970", R1_4, V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_28970_010-checkout-amex-footer.impex", languages, updateLanguagesOnly);
    }
}
