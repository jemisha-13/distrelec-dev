package com.namics.distrelec.patches.release.headless;

import static com.namics.distrelec.patches.structure.Release.HEADLESS;
import static com.namics.distrelec.patches.structure.StructureState.V0;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch2857 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch2857() {
        super("patch_2857", "patch_2857", HEADLESS, V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_2857_010-globalDataExample.impex", languages, updateLanguagesOnly);
    }

}
