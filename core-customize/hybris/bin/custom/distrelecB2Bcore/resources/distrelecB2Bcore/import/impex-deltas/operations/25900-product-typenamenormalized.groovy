import com.namics.distrelec.b2b.core.service.product.dao.DistProductDao
import com.namics.distrelec.b2b.core.util.DistSqlUtils
import de.hybris.platform.core.Registry
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.util.Config
import groovy.transform.Field
import org.apache.commons.lang3.time.DurationFormatUtils

import java.sql.Connection
import java.sql.PreparedStatement

import org.apache.logging.log4j.LogManager

import java.sql.ResultSet
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

ModelService modelService = spring.getBean(ModelService.class)
DistProductDao productDao = spring.getBean(DistProductDao.class)

LOG = LogManager.getLogger("product-typenamenormalized")

int updatedRecords

if (!Registry.hasCurrentTenant()) {
    Registry.activateMasterTenant()
}

if (Config.isSQLServerUsed()) {
    String selectSql = "select code from products where p_typename is not null and (p_typenamenormalized is null or p_typenamenormalized<>p_typename)"

    PreparedStatement getProductsStatement = connection.prepareStatement(selectSql)
    List<String> productsToTransform = new ArrayList<>()
    try {
        ResultSet rs = getProductsStatement.executeQuery()
        while (rs.next()) {
            String pk = rs.getString(1)
            productsToTransform.add(pk)
        }
        connection.commit()
    } finally {
        getProductsStatement.close()
    }

    int totalCount = productsToTransform.size()
    LOG.info("Total items found {}", totalCount)
    int remainingCount = totalCount
    Instant start = Instant.now()
    Instant batchStart = start;

    for (String productCode : productsToTransform) {
        List<ProductModel> products = productDao.findProductsByCode(productCode)
        for (ProductModel product : products) {
            connection.commit()
            product.setTypeName(product.getTypeName())
            modelService.save(product)
            remainingCount--
            updatedRecords++

            Duration elapsedBatch = Duration.between(batchStart, Instant.now())
            if (elapsedBatch.toMinutes() > 0) {
                connection.commit()
                batchStart = Instant.now()
                double processedPercentage = (double) (totalCount - remainingCount) / totalCount
                Duration elapsedDuration = Duration.between(start, Instant.now())
                Duration estimatedTotalDuration = Duration.of((long) ((double) elapsedDuration.toMillis() / processedPercentage), ChronoUnit.MILLIS)
                Duration remainingTime = estimatedTotalDuration.minusMillis(elapsedDuration.toMillis())

                LOG.info("Procesed {}%,\telapsed: {},\tremaining: {},\testimated total: {},\tremaining rows: {}",
                        String.format("%.2f", processedPercentage * 100.0),
                        DurationFormatUtils.formatDurationHMS(elapsedDuration.toMillis()),
                        DurationFormatUtils.formatDurationHMS(remainingTime.toMillis()),
                        DurationFormatUtils.formatDurationHMS(estimatedTotalDuration.toMillis()),
                        remainingCount);
            }
        }
    }
} else {
    String sql = "update products set p_typenamenormalized=" + DistSqlUtils.replaceRegexp("p_typename", "[^a-zA-Z0-9]", "") +
            " where p_typename is not null and (p_typenamenormalized is null or p_typenamenormalized<>" +
            DistSqlUtils.replaceRegexp("p_typename", "[^a-zA-Z0-9]", "") + ")"

    LOG.info("starting product typenamenormalized reset: " + sql)

    @Field Connection connection = Registry.getCurrentTenant().getDataSource().getConnection();
    PreparedStatement statement = connection.prepareStatement(sql);
    try {
        updatedRecords = statement.executeUpdate();
        connection.commit()
    } finally {
        statement.close()
    }
}

LOG.info("product typenamenormalized were reset. clearing cache")

Registry.getCurrentTenant().getCache().clear();

LOG.info("Cache was cleared")

return "Updated records: " + updatedRecords
