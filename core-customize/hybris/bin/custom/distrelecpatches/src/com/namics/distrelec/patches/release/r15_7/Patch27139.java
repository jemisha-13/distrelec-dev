package com.namics.distrelec.patches.release.r15_7;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch27139 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch27139() {
        super("patch_27139", "patch_27139", Release.R15_7, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_27139_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}
