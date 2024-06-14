package com.namics.distrelec.patches.release.r2_1;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch31418 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch31418() {
        super("patch_31418", "patch_31418", Release.R2_1, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_31418_010_add-bin-icons-for-disposal-of-packaging.impex", languages, updateLanguagesOnly);
    }
}
