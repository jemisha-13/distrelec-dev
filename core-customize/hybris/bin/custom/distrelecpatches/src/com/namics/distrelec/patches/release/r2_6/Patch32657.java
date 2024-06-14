package com.namics.distrelec.patches.release.r2_6;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch32657 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch32657() {
        super("patch_32657", "patch_32657", Release.R2_6, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_32657_010_add-new-cmrt-xlsx.impex", languages, updateLanguagesOnly);
    }
}
