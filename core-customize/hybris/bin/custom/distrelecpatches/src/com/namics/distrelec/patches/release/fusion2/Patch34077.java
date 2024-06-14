package com.namics.distrelec.patches.release.fusion2;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch34077 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch34077() {
        super("patch_34077", "patch_34077", Release.FUSION_2, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_34077_010_complete_migration_audit_job.impex", languages, updateLanguagesOnly);
    }
}
