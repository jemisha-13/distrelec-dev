package com.namics.distrelec.patches.release.r15_3;

import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import com.namics.distrelec.patches.release.AbstractDemoPatch;
import com.namics.distrelec.patches.release.SimpleDemoPatch;
import com.namics.distrelec.patches.structure.Release;
import com.namics.distrelec.patches.structure.StructureState;
import de.hybris.platform.cms2lib.model.components.AbstractBannerComponentModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.patches.organisation.ImportLanguage;
import de.hybris.platform.servicelayer.type.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@Component
public class Patch27394 extends AbstractDemoPatch implements SimpleDemoPatch {

    public static final int LOCALIZEDURLLINK_SIZE = 2000;

    @Autowired
    private DistSqlUtils distSqlUtils;

    @Autowired
    private TypeService typeService;

    public Patch27394() {
        super("patch_27394", "patch_27394", Release.R15_3, StructureState.V0);
    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        ComposedTypeModel abstractBannerComponentType = typeService.getComposedTypeForClass(AbstractBannerComponentModel.class);

        Collection<AttributeDescriptorModel> attrDescriptors = abstractBannerComponentType.getDeclaredattributedescriptors();
        attrDescriptors.stream()
                .filter(attrDesc -> AbstractBannerComponentModel.LOCALIZEDURLLINK.equals(attrDesc.getQualifier()))
                .forEach(this::increaseColumnSize);
    }

    protected void increaseColumnSize(AttributeDescriptorModel attrDescriptor) {
        String actionName = String.format("Increase column size for %s:%s",
                attrDescriptor.getDeclaringEnclosingType().getCode(),
                attrDescriptor.getName());

        String increaseColumnSizeQuery = distSqlUtils.getIncreaseVarcharColumnQuery(attrDescriptor, LOCALIZEDURLLINK_SIZE);

        executeUpdateOnDB(actionName, increaseColumnSizeQuery);
    }
}
