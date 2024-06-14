package com.namics.distrelec.patches.release.r1_9;

import de.hybris.platform.patches.organisation.ImportLanguage;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

@Component
public class Patch31169 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch31169() {
        super("patch_31169_v3", "patch_31169", Release.R1_9, StructureState.V0);
    }

    @Override
    public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly) {
        importGlobalData("rpatch_31169_010-email-logo-update.impex", languages, updateLanguagesOnly);
    }
}
