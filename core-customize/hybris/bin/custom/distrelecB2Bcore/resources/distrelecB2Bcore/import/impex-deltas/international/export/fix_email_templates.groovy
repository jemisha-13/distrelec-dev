import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageTemplateModel
import de.hybris.platform.commons.enums.RendererTypeEnum
import de.hybris.platform.commons.model.renderer.RendererTemplateModel
import de.hybris.platform.commons.renderer.daos.RendererTemplateDao
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult

// 1. - Create new renderer templates for FR

final String inputSite = "distrelec_CH"
final String outputSite = "distrelec_FR"
final List<Locale> locales = Arrays.asList(Locale.ENGLISH, Locale.FRENCH)

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class)
ModelService modelService = spring.getBean(ModelService.class)
RendererTemplateDao rendererTemplateDao = spring.getBean(RendererTemplateDao.class)

String findRendererTemplatesForSiteQueryText = "SELECT {t.pk} FROM {RendererTemplate AS t}" +
        "WHERE {t.code} LIKE ?site||'%'"

FlexibleSearchQuery findRendererTemplatesForSiteQuery = new FlexibleSearchQuery(findRendererTemplatesForSiteQueryText)
findRendererTemplatesForSiteQuery.addQueryParameter("site", inputSite)

SearchResult<RendererTemplateModel> findRendererTemplatesForSiteQueryResult = flexibleSearchService.search(findRendererTemplatesForSiteQuery)

findRendererTemplatesForSiteQueryResult.result.stream().forEach { inputTemplate ->
    String newCode = inputTemplate.getCode().replace(inputSite, outputSite)
    RendererTemplateModel outputTemplate = null;
    List<RendererTemplateModel> existingRendererTemplateList = rendererTemplateDao.findRendererTemplatesByCode(newCode)
    if (existingRendererTemplateList.isEmpty()) {
        outputTemplate = modelService.create(RendererTemplateModel.class)
    } else {
        outputTemplate = existingRendererTemplateList.get(0)
    }

    outputTemplate.setRendererType(RendererTypeEnum.VELOCITY)
    outputTemplate.setCode(newCode)
    outputTemplate.setContextClass(inputTemplate.getContextClass())

    for (Locale locale : locales) {
        outputTemplate.setTemplateScript(inputTemplate.getTemplateScript(locale), locale)
        outputTemplate.setDescription(inputTemplate.getDescription(locale), locale)
    }

    modelService.save(outputTemplate)
}

// 2. - Update all emails in FR staged content catalog to use new renderer templates

String findAllEmailTemplatesInOutputSiteQueryText = "SELECT {t.pk} FROM {EmailPageTemplate AS t " +
        "JOIN CatalogVersion AS cv ON {t.catalogVersion}={cv.pk}" +
        "JOIN Catalog AS c ON {cv.catalog}={c.pk}" +
        "}" +
        "WHERE {c.id}=?catalogId " +
        "AND {cv.version}='Staged'"

FlexibleSearchQuery findAllEmailTemplatesInOutputSiteQuery = new FlexibleSearchQuery(findAllEmailTemplatesInOutputSiteQueryText)
findAllEmailTemplatesInOutputSiteQuery.addQueryParameter("catalogId", outputSite + "ContentCatalog")

SearchResult<EmailPageTemplateModel> findAllEmailTemplatesInOutputSiteQueryResult = flexibleSearchService.search(findAllEmailTemplatesInOutputSiteQuery)

findAllEmailTemplatesInOutputSiteQueryResult.result.stream().forEach { template ->
    if (template.getSubject() != null && template.getHtmlTemplate() != null) {
        String currentSubjectCode = template.getSubject().getCode()
        String currentBodyCode = template.getHtmlTemplate().getCode()

        String newSubjectCode = currentSubjectCode.replace(inputSite, outputSite)
        String newBodyCode = currentBodyCode.replace(inputSite, outputSite)

        RendererTemplateModel newSubjectTemplate = rendererTemplateDao.findRendererTemplatesByCode(newSubjectCode).get(0)
        RendererTemplateModel newBodyTemplate = rendererTemplateDao.findRendererTemplatesByCode(newBodyCode).get(0)

        template.setSubject(newSubjectTemplate)
        template.setHtmlTemplate(newBodyTemplate)

        modelService.save(template)
    }else{
        println("Template ${template.getUid()} doesn't have subject and/or body template")
    }
}

return "DONE"