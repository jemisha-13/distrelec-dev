package com.namics.distrelec.patches.release.r3_2;

import java.util.Set;

import com.namics.distrelec.patches.constants.CatalogConstants;
import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch33554 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch33554() {
        super("patch_33554_1", "patch_33554", Release.R3_2, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_33554-quality-and-legal-cms-content.impex", languages, updateLanguagesOnly);
        registerCatalogForSynchronization(CatalogConstants.DISTRELEC_INT_CONTENTCATALOG);
    }
}
