package com.namics.distrelec.patches.release.upgrade2211;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch31849 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch31849() {
        super("patch_31849", "patch_31849", Release.UPGRADE_2211, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        executeGroovyScript("rpatch_31849-reset-nodeids-on-cronjobs.groovy");
    }
}
