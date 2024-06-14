package com.namics.distrelec.patches.release.r3_7;

import static com.namics.distrelec.patches.structure.Release.R3_7;
import static com.namics.distrelec.patches.structure.StructureState.V0;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch34616 extends AbstractDemoPatch implements SimpleDemoPatch {

    Patch34616() {
        super("patch_34616_2", "patch_34616", R3_7, V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_34616-ssr-blob-cache-cleanup.impex", languages, updateLanguagesOnly);
    }
}
