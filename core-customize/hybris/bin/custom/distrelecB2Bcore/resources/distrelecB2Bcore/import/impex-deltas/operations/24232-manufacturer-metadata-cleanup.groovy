import de.hybris.platform.core.Registry
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

import org.apache.logging.log4j.LogManager

LOG = LogManager.getLogger("manufacturer-meta-data-cleanup")

if (!Registry.hasCurrentTenant()) {
    Registry.activateMasterTenant()
}

sql = "DELETE FROM distmetadata dmetaData WHERE  dmetaData.p_manufacturer IS NOT NULL" 

LOG.info("removing manufacturer metadata")

Connection connection = Registry.getCurrentTenant().getDataSource().getConnection();
PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
        ResultSet.CONCUR_READ_ONLY);
statement.executeUpdate();

LOG.info("Manufacturer metadata clean up finished. ")

Registry.getCurrentTenant().getCache().clear();

LOG.info("Cache was cleared")


println "Cleanup Task Finished"