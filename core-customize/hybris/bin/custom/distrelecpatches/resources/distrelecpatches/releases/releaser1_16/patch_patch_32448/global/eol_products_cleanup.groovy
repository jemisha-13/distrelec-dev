import de.hybris.platform.core.Registry
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Field Logger LOG = LoggerFactory.getLogger("EOL PRODUCTS CLEANUP")

@Field Connection connection = null

@Field Instant startTime = Instant.now();
@Field Instant sinceLastAction = Instant.now();

@Field batchSize = 1000
// If set to -1 all batches will be executed
@Field final int maxBatches = -1
@Field int count = 1

try {
    connection = Registry.getCurrentTenant().getDataSource().getConnection()
    count = 1;
    Instant salesOrgCleanupStart = Instant.now()
    deleteEOLSalesOrgProducts()
    Duration salesOrgCleanupDuration = Duration.between(salesOrgCleanupStart, Instant.now())
    LOG.info("Sales org cleanup duration {}", formatDuration(salesOrgCleanupDuration))

    count=1;
    Instant productCleanupStart = Instant.now()
    deleteEOLProducts()
    Duration productCleanupDuration = Duration.between(productCleanupStart, Instant.now())
    LOG.info("Product cleanup duration {}", formatDuration(productCleanupDuration))

    count = 1;
    Instant productLocalizationsCleanupStart = Instant.now()
    deleteEOLProductsLocalizations()
    Duration productLocalizationsCleanupDuration = Duration.between(productLocalizationsCleanupStart, Instant.now())
    LOG.info("Product localizations cleanup duration {}", formatDuration(productLocalizationsCleanupDuration))

    count = 1;
    Instant stockCleanupStart = Instant.now()
    deleteStocks()
    Duration stockCleanupDuration = Duration.between(stockCleanupStart, Instant.now())
    LOG.info("Stock cleanup duration {}", formatDuration(stockCleanupDuration))

    count = 1;
    Instant pricesCleanupStart = Instant.now()
    deletePrices()
    Duration pricesCleanupDuration = Duration.between(pricesCleanupStart, Instant.now())
    LOG.info("Prices cleanup duration {}", formatDuration(pricesCleanupDuration))

    count = 1;
    Instant featuresCleanupStart = Instant.now()
    deleteProductFeatures()
    Duration featuresCleanupDuration = Duration.between(featuresCleanupStart, Instant.now())
    LOG.info("Features cleanup duration {}", formatDuration(featuresCleanupDuration))

    connection.commit()
    Duration duration = Duration.between(startTime, Instant.now())
    LOG.info("Finished in {}", String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart()))
} catch (Exception e){
    LOG.error("EOL cleanup failed. Error {}", e.message, e);
}finally {
    if (connection != null) {
        connection.close()
    }
}

static String formatDuration(Duration duration){
    return String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart())
}

def printDurations(){
    Duration lastActionDuration = Duration.between(sinceLastAction, Instant.now());
    Duration totalDuration = Duration.between(startTime, Instant.now());
    LOG.info("Last action duration {}", formatDuration(lastActionDuration))
    LOG.info("Total duration {}", formatDuration(totalDuration))
    sinceLastAction = Instant.now()
}

def deleteEOLSalesOrgProducts() {
    LOG.info("Removing sales org products")
    String query = """
    delete from DISTSALESORGPRODUCT where pk in (
        select dsp.pk from DISTSALESORGPRODUCT dsp
    where (exists(select 1
                  from DISTCODELIST ss
                  where ss.PK = dsp.P_SALESSTATUS
                    and ss.P_ENDOFLIFEINSHOP = 1)
               and ((exists(select 1
                            from PRODUCTREFERENCES pr
                            where pr.P_SOURCE = dsp.P_PRODUCT)
            and dsp.P_ENDOFLIFEDATE < ?))
        or ((not exists(select 1
                        from PRODUCTREFERENCES pr
                        where pr.P_SOURCE = dsp.P_PRODUCT))
            and dsp.P_ENDOFLIFEDATE < ?))
        order by dsp.pk
        offset 0 rows
        fetch first $batchSize rows only
    )
    """
    PreparedStatement statement = connection.prepareStatement(query)
    statement.setTimestamp(1, Timestamp.from(getPastDate(18)))
    statement.setTimestamp(2, Timestamp.from(getPastDate(12)))

    int removedItemsCount = statement.executeUpdate()
    statement.close()
    connection.commit()
    LOG.info("Removed {} DISTSALESORGPRODUCT", removedItemsCount)
    printDurations()
    if(removedItemsCount > 0 && (maxBatches < 0 || count++ < (maxBatches * 50))){
        deleteEOLSalesOrgProducts()
    }
}

static Instant getPastDate(final int months) {
    final LocalDate localDate = LocalDate.now().minusMonths(months).minusDays(1L)
    final ZoneId defaultZoneId = ZoneId.systemDefault()
    return localDate.atStartOfDay(defaultZoneId).toInstant()
}

def deleteEOLProducts() {
    LOG.info("Removing products")
    String query = """    
    delete from PRODUCTS where pk in (
    select p.pk from PRODUCTS p where not exists(select 1
                     from DISTSALESORGPRODUCT dsp
                     where dsp.P_PRODUCT = p.PK)
    order by p.pk
    offset 0 rows
    fetch first $batchSize rows only
    )
    """
    PreparedStatement statement = connection.prepareStatement(query)
    int removedItemsCount = statement.executeUpdate()
    statement.close()
    connection.commit()
    printDurations()
    LOG.info("Removed {} PRODUCTS", removedItemsCount)
    if(removedItemsCount > 0 && (maxBatches < 0 || count++ < (maxBatches * 50))){
        deleteEOLProducts()
    }
}

def deleteEOLProductsLocalizations() {
    LOG.info("Removing product localizations")
    String locQuery = """    
    delete from PRODUCTSLP where itempk in (
    select p.itempk from PRODUCTSLP p where not exists(select 1
                     from DISTSALESORGPRODUCT dsp
                     where dsp.P_PRODUCT = p.ITEMPK)
    order by p.itempk
    offset 0 rows
    fetch first $batchSize rows only
    )
    """
    PreparedStatement statement = connection.prepareStatement(locQuery)
    int removedItemsCount = statement.executeUpdate()
    statement.close()
    connection.commit()
    printDurations()
    LOG.info("Removed {} PRODUCTS LOCALIZATIONS", removedItemsCount)
    if(removedItemsCount > 0 && (maxBatches < 0 || count++ < maxBatches)){
        deleteEOLProductsLocalizations()
    }
}

def deleteStocks() {
    LOG.info("Removing stocks")
    String query = """    
    delete from STOCKLEVELS where pk in (
    select sl.pk from STOCKLEVELS sl where not exists(select 1
                     from PRODUCTS p
                     where sl.P_PRODUCTCODE=p.CODE)
    order by sl.pk
    offset 0 rows
    fetch first $batchSize rows only
    )
    """
    PreparedStatement statement = connection.prepareStatement(query)
    int removedItemsCount = statement.executeUpdate()
    statement.close()
    connection.commit()

    LOG.info("Removed {} STOCKLEVELS", removedItemsCount)
    printDurations()
    if(removedItemsCount > 0 && (maxBatches < 0 || count++ < maxBatches)){
        deleteStocks()
    }
}

def deletePrices() {
    LOG.info("Removing prices")
    String query = """    
    delete from PRICEROWS where pk in (
    select pr.pk from PRICEROWS pr where not exists(select 1
                     from PRODUCTS p
                     where pr.P_PRODUCT=p.PK)
    order by pr.pk
    offset 0 rows 
    fetch first $batchSize rows only
    )
    """
    PreparedStatement statement = connection.prepareStatement(query)
    int removedItemsCount = statement.executeUpdate()
    statement.close()
    connection.commit()

    LOG.info("Removed {} PRICEROWS", removedItemsCount)
    printDurations()
    if(removedItemsCount > 0 && (maxBatches < 0 || count++ < maxBatches)){
        deletePrices()
    }
}

def deleteProductFeatures() {
    LOG.info("Removing product features")
    String query = """    
    delete from PRODUCTFEATURES where pk in (
    select pf.pk from PRODUCTFEATURES pf where not exists(select 1  
                     from PRODUCTS p 
                     where pf.P_PRODUCT=p.PK)
    order by pf.pk
    offset 0 rows
    fetch first $batchSize rows only
    )
    """
    PreparedStatement statement = connection.prepareStatement(query)
    int removedItemsCount = statement.executeUpdate()
    statement.close()
    connection.commit()

    LOG.info("Removed {} PRODUCTFEATURES", removedItemsCount)
    printDurations()
    if(removedItemsCount > 0 && (maxBatches < 0 || count++ < maxBatches)){
        deleteProductFeatures()
    }
}
