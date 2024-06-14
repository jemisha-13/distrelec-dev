import de.hybris.platform.core.Registry
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

import org.apache.logging.log4j.LogManager

LOG = LogManager.getLogger("calibration-products-reset")

if (!Registry.hasCurrentTenant()) {
    Registry.activateMasterTenant()
}

sql = "update productslp plp set p_pimxmlhashlocalized=null " +
    " where exists (select * from products p where plp.itempk=p.pk and p.p_calibrationservice is not null) " +
    " and exists (select * from languages l where plp.langpk=l.pk and l.isocode='en')"

LOG.info("starting calibration product reset")

Connection connection = Registry.getCurrentTenant().getDataSource().getConnection();
PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
        ResultSet.CONCUR_READ_ONLY);
statement.executeUpdate();

LOG.info("calibration products were reset. clearing cache")

Registry.getCurrentTenant().getCache().clear();

LOG.info("Cache was cleared")
