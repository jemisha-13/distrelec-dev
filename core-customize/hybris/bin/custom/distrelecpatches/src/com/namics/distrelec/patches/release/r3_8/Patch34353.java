package com.namics.distrelec.patches.release.r3_8;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;

import de.hybris.platform.patches.organisation.ImportLanguage;

import static com.namics.distrelec.patches.structure.Release.R3_8;
import static com.namics.distrelec.patches.structure.StructureState.V0;

@Component
public class Patch34353 extends AbstractDemoPatch implements SimpleDemoPatch {

    Patch34353() {
        super("patch_34353", "patch_34353", R3_8, V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_34353-orphaned-price-row-removal-cronjob.impex", languages, updateLanguagesOnly);
    }
}
