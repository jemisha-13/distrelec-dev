package com.namics.distrelec.patches.release.r1_8;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch29639 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch29639() {
        super("patch_29639", "patch_29639", Release.R1_8, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_29639_010-bom-tool-ariba-oci-restriction.impex", languages, updateLanguagesOnly);
    }
}
