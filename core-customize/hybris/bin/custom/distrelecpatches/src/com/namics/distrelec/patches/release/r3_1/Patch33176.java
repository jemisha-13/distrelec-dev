package com.namics.distrelec.patches.release.r3_1;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch33176 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch33176() {
        super("patch_33176_1", "patch_33176", Release.R3_1, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_33176_010-fusion-export-readonly-datasource.impex", languages, updateLanguagesOnly);
    }
}
