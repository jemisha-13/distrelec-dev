package com.namics.distrelec.patches.release.r3_5;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.namics.distrelec.patches.constants.CatalogConstants.DISTRELEC_INT_CONTENTCATALOG;

@Component
public class Patch34138 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch34138() {
        super("patch_34138_2", "patch_34138", Release.R3_6, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_34138-restriction-group-component.impex", languages, updateLanguagesOnly);
        registerCatalogForSynchronization(DISTRELEC_INT_CONTENTCATALOG);
    }
}
