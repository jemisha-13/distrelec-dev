package com.namics.distrelec.patches.release.r3_5;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch34380 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch34380() {
        super("patch_34380", "patch_34380", Release.R3_5, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_34380_010-remove-attribute-descriptor.impex", languages, updateLanguagesOnly);
    }
}
