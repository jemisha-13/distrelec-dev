package com.namics.distrelec.patches.release.upgrade2211;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.patches.organisation.ImportLanguage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Patch32472 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch32472() {
        super("patch_32472_3", "patch_32472", Release.UPGRADE_2211, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("essentialdataMediaFolders.impex", languages, updateLanguagesOnly);
        importGlobalData("essentialdataThemesExtensions.impex", languages, updateLanguagesOnly);
    }
}
