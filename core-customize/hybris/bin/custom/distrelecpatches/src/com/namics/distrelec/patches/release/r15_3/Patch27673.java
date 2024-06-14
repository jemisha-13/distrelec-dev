package com.namics.distrelec.patches.release.r15_3;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch27673 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch27673() {
        super("patch_27673_2", "patch_27673", Release.R15_3, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_27673_010-globalDataExample.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_27673_020-globalDataExample.impex", languages, updateLanguagesOnly);
    }
}  
