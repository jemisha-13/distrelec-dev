package com.namics.distrelec.b2b.facades.permissions.impl;

import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.permissionsfacades.data.CatalogPermissionsData;
import de.hybris.platform.permissionsfacades.data.SyncPermissionsData;
import de.hybris.platform.permissionsfacades.impl.DefaultPermissionsFacade;

import java.util.List;

public class DefaultDistPermissionsFacade extends DefaultPermissionsFacade {

    @Override
    protected CatalogPermissionsData generateCatalogPermissionsDTO(CatalogVersionModel cv, boolean readPermission,
            boolean writePermission, PrincipalModel principal, List<SyncPermissionsData> syncPermissions) {
        CatalogPermissionsData permissions =
                super.generateCatalogPermissionsDTO(cv, readPermission, writePermission, principal, syncPermissions);

        String catalogId = cv.getCatalog().getId();
        if (DistUtils.containsUnderscore(catalogId)) {
            String convertedCatalogId = DistUtils.convertCatalogIdUnderscoreToMinus(catalogId);
            permissions.setCatalogId(convertedCatalogId);
        }

        return permissions;
    }
}
