import de.hybris.platform.core.Registry
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import com.namics.distrelec.b2b.core.constants.DistConstants.Product.ReplacementReasonCode

import org.apache.logging.log4j.LogManager

LOG = LogManager.getLogger("19707-reset-calibrated-flag.groovy")

if (!Registry.hasCurrentTenant()) {
    Registry.activateMasterTenant()
}

sql = "update products p set p_calibrated=1 " +
    " where p.p_calibrationservice=1" +
    " and exists (" +
        " select pr.pk from productreferences pr where pr.p_source=p.pk and exists(" +
            " select rr.pk from distcodelist rr where pr.p_replacementreason=rr.pk and rr.p_code='" + ReplacementReasonCode.NOT_CALIBRATED_INSTRUMENTS + "'" +
              " and exists (select ct.pk from composedtypes ct where ct.internalcode='DistReplacementReason' and rr.typePkString=ct.pk)" +
        ")" +
    ")"

LOG.info("starting setting calibrated flag")

Connection connection = Registry.getCurrentTenant().getDataSource().getConnection();
PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
        ResultSet.CONCUR_READ_ONLY);
statement.executeUpdate();

LOG.info("calibrated flag was reset. clearing cache")

Registry.getCurrentTenant().getCache().clear();

LOG.info("Cache was cleared")
