package com.namics.distrelec.b2b.facades.pagetemplates;

import de.hybris.platform.cmsfacades.data.PageTemplateDTO;
import de.hybris.platform.cmsfacades.data.PageTemplateData;
import de.hybris.platform.cmsfacades.pagetemplates.impl.DefaultPageTemplateFacade;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DistPageTemplateFacade extends DefaultPageTemplateFacade {

    @Override
    public List<PageTemplateData> findPageTemplates(PageTemplateDTO pageTemplateDTO) {
        List<PageTemplateData> pageTemplates = super.findPageTemplates(pageTemplateDTO);
        return pageTemplates.stream().sorted(Comparator.comparing(PageTemplateData::getName))
                .collect(Collectors.toList());
    }
}
