import com.google.common.collect.Lists
import com.namics.distrelec.b2b.core.model.DistManufacturerCountryModel
import com.namics.distrelec.b2b.core.model.DistManufacturerModel
import com.namics.distrelec.b2b.core.service.manufacturer.dao.DistManufacturerDao
import de.hybris.platform.core.model.c2l.CountryModel
import de.hybris.platform.servicelayer.i18n.daos.CountryDao
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

Logger LOG = LoggerFactory.getLogger("19672-return-manufacturers.groovy")

CountryDao countryDao = spring.getBean(CountryDao.class)
FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class)
DistManufacturerDao manufacturerDao = spring.getBean(DistManufacturerDao.class)
ModelService modelService = spring.getBean(ModelService.class)

String findMissingManufacturers = "SELECT DISTINCT {manu.code},{country.isocode}\n" +
        "FROM {DistManufacturer AS manu}, {CMSSite AS cmssite JOIN Country AS country ON {cmssite.country}={country.pk} JOIN DistSalesOrg AS sorg ON {cmssite.salesOrg}={sorg.pk}}\n" +
        "WHERE EXISTS ({{\n" +
        "  SELECT {product.pk} FROM {Product AS product JOIN DistSalesOrgProduct  AS salesOrgP ON {salesOrgP.product} = {product.pk}  JOIN DistSalesStatus AS salesSts ON {salesOrgP.salesStatus}={salesSts.pk}} WHERE {manu.pk}={product.manufacturer} AND {salesOrgP.salesOrg}={sorg.pk} AND {salesSts.visibleInShop} = 1 and  {salesSts.endOfLifeInShop} = 0 and {salesSts.buyableInShop} = 1 and NOT EXISTS ({{ SELECT {dcpf.pk} FROM {DistCOPunchOutFilter as dcpf} where {dcpf.product}={product.pk} and {dcpf.salesOrg }={sorg.pk} }})\n" +
        "  }})\n" +
        "AND NOT EXISTS ({{\n" +
        "SELECT {mancountry.pk}\n" +
        "FROM { DistManufacturerCountry as mancountry}\n" +
        "where {mancountry.manufacturer}={manu.pk}\n" +
        "AND {mancountry.country}={country.pk}\n" +
        "}})"

final FlexibleSearchQuery newquery = new FlexibleSearchQuery(findMissingManufacturers);
newquery.setResultClassList(Lists.newArrayList(String.class, String.class));
final List<List<String>> manufacturerList = flexibleSearchService.<List<String>>search(newquery).getResult();

for (List<String> manufacturerEntry : manufacturerList) {
    String manufacturerCode = manufacturerEntry.get(0)
    String countryIsoCode = manufacturerEntry.get(1)

    LOG.info("assign manufacturer " + manufacturerCode + " to country " + countryIsoCode)
    CountryModel country = countryDao.findCountriesByCode(countryIsoCode).get(0)
    DistManufacturerModel manufacturer = manufacturerDao.findManufacturerByCode(manufacturerCode)

    DistManufacturerCountryModel manCountry = new DistManufacturerCountryModel()
    manCountry.country = country
    manCountry.manufacturer = manufacturer
    manCountry.visibleOnShop = true
    modelService.save(manCountry)
}
