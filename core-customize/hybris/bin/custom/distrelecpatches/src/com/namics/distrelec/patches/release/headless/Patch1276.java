package com.namics.distrelec.patches.release.headless;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch1276 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch1276() {
        super("patch_1276_2", "patch_1276", Release.HEADLESS, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_1276_010-cms-content-manufacturer-details.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_1276_010-cms-content-outlet.impex", languages, updateLanguagesOnly);
    }
}
