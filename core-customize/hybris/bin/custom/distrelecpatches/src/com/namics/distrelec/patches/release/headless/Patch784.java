package com.namics.distrelec.patches.release.headless;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch784 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch784() {
        super("patch_784", "patch_784", Release.HEADLESS, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        executeGroovyScript("patch_784-upgrade-footer-components.groovy");
    }
}
