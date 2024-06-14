package com.namics.distrelec.patches.release.upgrade2211;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch30162 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch30162() {
        super("patch_30162", "patch_30162", Release.UPGRADE_2211, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_30162_010-remove-attribute-descriptors.impex", languages, updateLanguagesOnly);
    }
}
