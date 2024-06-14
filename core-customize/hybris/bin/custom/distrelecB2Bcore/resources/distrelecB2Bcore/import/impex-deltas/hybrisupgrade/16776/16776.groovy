import de.hybris.platform.commerceservices.setup.SetupImpexService
import de.hybris.platform.commerceservices.setup.SetupSyncJobService
import de.hybris.platform.servicelayer.impex.ImportConfig
import de.hybris.platform.servicelayer.impex.ImportResult
import de.hybris.platform.servicelayer.impex.ImportService
import de.hybris.platform.servicelayer.impex.impl.StreamBasedImpExResource
import org.apache.commons.io.IOUtils

String IMPEX_ROOT = "/distrelecB2Bcore/import/impex-deltas/hybrisupgrade/16776"

SetupImpexService impexService = spring.getBean("defaultSetupImpexService")
impexService.importImpexFile("${IMPEX_ROOT}/16776_1.impex", true)

SetupSyncJobService setupSyncJobService = spring.getBean("setupSyncJobService")
setupSyncJobService.executeCatalogSyncJob("distrelec_IntContentCatalog")

impexService.importImpexFile("${IMPEX_ROOT}/16776_2.impex", true)

def prepareMacroHeader(Map<String, Object> macroMap) {
    StringBuilder sb = new StringBuilder();

    macroMap.forEach{ key, value ->
        sb.append("\$$key = ${value.toString()}\n")
    }

    return sb.toString()
}

def importImpex (String filename, Map<String,Object> macroMap){
    InputStream inputStream = SetupImpexService.class.getResourceAsStream(filename);

    if(inputStream != null){
        def macroHeader = prepareMacroHeader(macroMap)
        def macroHeaderInputStream = IOUtils.toInputStream(macroHeader)
        def combinedInputStream = new SequenceInputStream(macroHeaderInputStream, inputStream)

        ImportService importService = spring.getBean("importService")
        ImportConfig importConfig = new ImportConfig()
        importConfig.setScript(new StreamBasedImpExResource(combinedInputStream, "UTF-8"))
        importConfig.setLegacyMode(false)
        ImportResult importResult = importService.importData(importConfig)

        if(importResult.error && !importResult.hasUnresolvedLines()){
            println("Failed to import $filename.")
        }
        if(importResult.hasUnresolvedLines()){
            println("Partially imported $filename.")
        }
    }else{
        println("Failed to import $filename. File not found")
    }
}

def countryPrefixes = ["AT", "BE", "CH", "CZ", "DE", "DK", "EE", "EX", "FI", "HU", "IT", "LT", "LV", "NL", "NO", "PL", "RO", "SE", "SK"]

countryPrefixes.forEach { countryPrefix ->
    println("Importing data for $countryPrefix")
    def macroMap = [countryPrefix: countryPrefix]
    importImpex("${IMPEX_ROOT}/16776_3.impex", macroMap)
}

return "FINISHED"