import de.hybris.platform.b2b.model.B2BUnitModel
import de.hybris.platform.core.model.c2l.CountryModel
import de.hybris.platform.servicelayer.i18n.CommonI18NService
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult

ModelService modelService = spring.getBean(ModelService.class)
FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class)

/*

SELECT pk FROM

({{SELECT {bu.pk} AS pk

FROM {B2BUnit AS bu
	JOIN CustomerType AS ct ON {bu.customerType}={ct.pk}
    JOIN DistSalesOrg AS so ON {bu.salesOrg}={so.pk}
    JOIN B2BCustomer AS c ON {c.defaultB2BUnit}={bu.pk}
    JOIN Address AS bAddr ON {bu.billingAddress}={bAddr.pk}
    JOIN Country AS bAddrC ON {bAddr.country}={bAddrC.pk}
    JOIN Address AS sAddr ON {bu.shippingAddress}={sAddr.pk}
    JOIN Country AS sAddrC ON {sAddr.country}={sAddrC.pk}}

    WHERE {so.code} = '7801'
    AND {ct.code} = 'B2B'
    AND {bu.country} IS NULL
    AND {bAddrC.isocode} = 'FR'
    AND {sAddrC.isocode} != 'FR'}}

UNION

{{SELECT {bu.pk} AS pk

FROM {B2BUnit AS bu
	JOIN CustomerType AS ct ON {bu.customerType}={ct.pk}
    JOIN DistSalesOrg AS so ON {bu.salesOrg}={so.pk}
    JOIN B2BCustomer AS c ON {c.defaultB2BUnit}={bu.pk}
    JOIN Address AS bAddr ON {bu.billingAddress}={bAddr.pk}
    JOIN Country AS bAddrC ON {bAddr.country}={bAddrC.pk}
    JOIN Address AS sAddr ON {bu.shippingAddress}={sAddr.pk}
    JOIN Country AS sAddrC ON {sAddr.country}={sAddrC.pk}}

    WHERE {so.code} = '7801'
    AND {ct.code} = 'B2B'
    AND {bu.country} IS NULL
    AND {bAddrC.isocode} = 'FR'
    AND {sAddrC.isocode} = 'FR'}})

 */

String queryText = "SELECT pk FROM\n" +
        "\n" +
        "({{SELECT {bu.pk} AS pk\n" +
        "\n" +
        "FROM {B2BUnit AS bu\n" +
        "\tJOIN CustomerType AS ct ON {bu.customerType}={ct.pk}\n" +
        "    JOIN DistSalesOrg AS so ON {bu.salesOrg}={so.pk}\n" +
        "    JOIN B2BCustomer AS c ON {c.defaultB2BUnit}={bu.pk}\n" +
        "    JOIN Address AS bAddr ON {bu.billingAddress}={bAddr.pk}\n" +
        "    JOIN Country AS bAddrC ON {bAddr.country}={bAddrC.pk}\n" +
        "    JOIN Address AS sAddr ON {bu.shippingAddress}={sAddr.pk}\n" +
        "    JOIN Country AS sAddrC ON {sAddr.country}={sAddrC.pk}}\n" +
        "    \n" +
        "    WHERE {so.code} = '7801'\n" +
        "    AND {ct.code} = 'B2B'\n" +
        "    AND {bu.country} IS NULL\n" +
        "    AND {bAddrC.isocode} = 'FR' \n" +
        "    AND {sAddrC.isocode} != 'FR'}}\n" +
        "\n" +
        "UNION\n" +
        "\n" +
        "{{SELECT {bu.pk} AS pk\n" +
        "\n" +
        "FROM {B2BUnit AS bu\n" +
        "\tJOIN CustomerType AS ct ON {bu.customerType}={ct.pk}\n" +
        "    JOIN DistSalesOrg AS so ON {bu.salesOrg}={so.pk}\n" +
        "    JOIN B2BCustomer AS c ON {c.defaultB2BUnit}={bu.pk}\n" +
        "    JOIN Address AS bAddr ON {bu.billingAddress}={bAddr.pk}\n" +
        "    JOIN Country AS bAddrC ON {bAddr.country}={bAddrC.pk}\n" +
        "    JOIN Address AS sAddr ON {bu.shippingAddress}={sAddr.pk}\n" +
        "    JOIN Country AS sAddrC ON {sAddr.country}={sAddrC.pk}}\n" +
        "    \n" +
        "    WHERE {so.code} = '7801'\n" +
        "    AND {ct.code} = 'B2B'\n" +
        "    AND {bu.country} IS NULL\n" +
        "    AND {bAddrC.isocode} = 'FR' \n" +
        "    AND {sAddrC.isocode} = 'FR'}})"

FlexibleSearchQuery query = new FlexibleSearchQuery(queryText)

SearchResult<B2BUnitModel> queryResult = flexibleSearchService.search(query)

CommonI18NService commonI18NService = spring.getBean(CommonI18NService.class)

CountryModel franceCountry = commonI18NService.getCountry("FR")

queryResult.getResult().stream().forEach {
    it.setCountry(franceCountry)
    modelService.save(it)
}

return "Assigned France to ${queryResult.count} b2b units"