package com.namics.distrelec.patches.release.headless;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch2992 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch2992() {
        super("patch_2992", "patch_2992", Release.HEADLESS, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_2992_010-remove-attribute-descriptor-alternative-design.impex", languages, updateLanguagesOnly);
    }
}
