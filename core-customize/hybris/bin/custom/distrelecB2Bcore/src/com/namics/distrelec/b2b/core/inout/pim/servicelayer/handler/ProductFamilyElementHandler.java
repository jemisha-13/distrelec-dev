package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ProductFamilyElementHandler extends AbstractHashAwarePimImportElementHandler {

    private static final String FAMILY_TYPE = "Familie";

    @Autowired
    private DistCategoryService distCategoryService;

    @Autowired
    private DistrelecCodelistService distrelecCodelistService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private UserService userService;

    public ProductFamilyElementHandler() {
        super(FAMILY_TYPE);
    }

    @Override
    protected CategoryModel getModel(String id, Element element) {
        return distCategoryService.findProductFamily(id).orElseGet(() -> createNewProductFamily(id));
    }

    private CategoryModel createNewProductFamily(String code) {
        CategoryModel productFamily = modelService.create(CategoryModel.class);
        productFamily.setPimCategoryType(distrelecCodelistService.getDistPimCategoryType(FAMILY_TYPE));
        productFamily.setCode(code);
        productFamily.setCatalogVersion(getImportContext().getProductCatalogVersion());
        setVisible(productFamily);

        return productFamily;
    }

    private void setVisible(CategoryModel productFamily) {
        boolean customerGroupAssigned = Optional.ofNullable(productFamily.getAllowedPrincipals())
                .map(Collection::stream)
                .orElse(Stream.empty())
                .anyMatch(p -> p.getUid().equals(DistConstants.User.B2CCUSTOMERGROUP_UID));
        if (customerGroupAssigned) {
            return;
        }
        List<PrincipalModel> newAllowedPrincipals = new ArrayList<>();
        newAllowedPrincipals.add(userService.getUserGroupForUID(DistConstants.User.B2CCUSTOMERGROUP_UID));
        if (productFamily.getAllowedPrincipals() != null) {
            newAllowedPrincipals.addAll(productFamily.getAllowedPrincipals());
        }
        productFamily.setAllowedPrincipals(newAllowedPrincipals);
    }
}
