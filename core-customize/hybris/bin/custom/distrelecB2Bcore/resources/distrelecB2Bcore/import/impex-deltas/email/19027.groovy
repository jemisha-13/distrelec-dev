import de.hybris.platform.cms2.servicelayer.services.CMSSiteService
import de.hybris.platform.commons.model.renderer.RendererTemplateModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchService

import java.nio.charset.StandardCharsets

CMSSiteService cmsSiteService = spring.getBean('cmsSiteService')
ModelService modelService = spring.getBean('modelService')
FlexibleSearchService flexibleSearchService = spring.getBean('flexibleSearchService')

def sites = cmsSiteService.getSites()

def static convertInputStreamToString(InputStream inputStream) {
    Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())
    scanner.useDelimiter("\\A").next()
}
def static readTemplateScript() {
    convertInputStreamToString(ModelService.class.getClassLoader().getResourceAsStream("distrelecB2Bcore/import/project/contentCatalogs/distrelecContentCatalog/emails/email-sendToFriendBody.vm"))
}

for (def site : sites) {
    if (site.getCountryContentCatalog() == null) {
        println "No country content catalog for site uid: ${site.getUid()}. Continuing"
        continue
    }
    RendererTemplateModel target = modelService.create(RendererTemplateModel.class)
    target.setCode("${site.getUid()}-Email_Send_To_Friend_Body")
    try {
        target = flexibleSearchService.getModelByExample(target)
    } catch (Exception ex) {
        println "Could not find tempate for site uid ${site.getUid()}. Continuing"
        continue
    }
    for (def lang : site.getCountryContentCatalog().getLanguages()) {
        target.setTemplateScript(readTemplateScript(), new Locale(lang.isocode))
    }
    modelService.save(target)
}