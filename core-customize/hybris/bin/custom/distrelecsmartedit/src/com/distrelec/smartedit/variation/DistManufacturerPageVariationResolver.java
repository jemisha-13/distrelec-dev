package com.distrelec.smartedit.variation;

import com.namics.distrelec.b2b.core.model.pages.DistManufacturerPageModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSPageDao;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.OptionData;
import de.hybris.platform.cmsfacades.page.DisplayCondition;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DistManufacturerPageVariationResolver implements PageVariationResolver<DistManufacturerPageModel> {

    @Autowired
    private TypeService typeService;

    @Autowired
    private CMSPageDao cmsPageDao;

    @Autowired
    private CMSAdminPageService adminPageService;

    @Override
    public List<DistManufacturerPageModel> findPagesByType(String typeCode, boolean isDefaultPage) {
        final ComposedTypeModel type = typeService.getComposedTypeForCode(typeCode);
        final List<DistManufacturerPageModel> pages = cmsPageDao.findAllPagesByTypeAndCatalogVersions(type, Arrays.asList(adminPageService.getActiveCatalogVersion())).stream()
                .map(abstractPage -> (DistManufacturerPageModel) abstractPage)
                .collect(Collectors.toList());

        List<DistManufacturerPageModel> defaultPages = findDefaultPages(pages);
        if (isDefaultPage) {
            return defaultPages;
        } else {
            return pages.stream().filter(page -> !defaultPages.contains(page)).collect(Collectors.toList());
        }
    }

    protected List<DistManufacturerPageModel> findDefaultPages(final List<DistManufacturerPageModel> contentPages) {
        return contentPages.stream()
                .filter(page -> CollectionUtils.isEmpty(page.getRestrictions()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DistManufacturerPageModel> findDefaultPages(DistManufacturerPageModel page) {
        return findPagesByType(DistManufacturerPageModel._TYPECODE, true);
    }

    @Override
    public List<DistManufacturerPageModel> findVariationPages(DistManufacturerPageModel page) {
        return findPagesByType(DistManufacturerPageModel._TYPECODE, false);
    }

    @Override
    public boolean isDefaultPage(DistManufacturerPageModel page) {
        return CollectionUtils.isEmpty(page.getRestrictions());
    }

    @Override
    public List<OptionData> findDisplayConditions(String typeCode) {
        final List<DistManufacturerPageModel> defaultPages = findPagesByType(typeCode, Boolean.TRUE);

        final List<OptionData> options = new ArrayList<>();
        if (CollectionUtils.isEmpty(defaultPages)) {
            createOptionData(DisplayCondition.PRIMARY.name(), CmsfacadesConstants.PAGE_DISPLAY_CONDITION_PRIMARY, options);
        } else {
            createOptionData(DisplayCondition.VARIATION.name(), CmsfacadesConstants.PAGE_DISPLAY_CONDITION_VARIATION, options);
        }
        return options;
    }

    protected void createOptionData(final String id, final String label, final List<OptionData> options) {
        final OptionData optionData = new OptionData();
        optionData.setId(id);
        optionData.setLabel(label);
        options.add(optionData);
    }

}
