package com.namics.distrelec.patches.release.r3_3;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.constants.CatalogConstants;
import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch34240 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch34240() {
        super("patch_34240", "patch_34240", Release.R3_3, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_34240-rs-content-slot.impex", languages, updateLanguagesOnly);
        registerCatalogsForSynchronization(CatalogConstants.ALL_CONTENT_CONTENTCATALOGS);
    }
}
