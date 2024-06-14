package com.namics.distrelec.patches.release.r3_4;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;

import de.hybris.platform.patches.organisation.ImportLanguage;

@Component
public class Patch33472 extends AbstractDemoPatch implements SimpleDemoPatch {

    public Patch33472() {
        super("patch_33472_6", "patch_33472", Release.R3_4, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        importGlobalData("rpatch_33472_010-enable-registration-cms-site.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_33472_020-enable-cart-cms-site.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_33472_030-enable-newsletter-cms-site.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_33472_040-enable-my-account-redirect-cms-site.impex", languages, updateLanguagesOnly);
        importGlobalData("rpatch_33472_050-enable-product-return-cms-site.impex", languages, updateLanguagesOnly);
    }
}
