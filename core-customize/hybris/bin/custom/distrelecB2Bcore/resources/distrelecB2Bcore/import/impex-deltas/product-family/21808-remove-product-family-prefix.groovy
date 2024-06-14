import de.hybris.platform.core.Registry

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

if (!Registry.hasCurrentTenant()) {
    Registry.activateMasterTenant()
}

String updateSql = "update products set p_pimfamilycategorycode=substr(p_pimfamilycategorycode,5)" +
        " where p_pimfamilycategorycode like 'cat-%'"

try {
    Connection connection = Registry.getCurrentTenant().getDataSource().getConnection();
    PreparedStatement statement = connection.prepareStatement(updateSql, ResultSet.TYPE_FORWARD_ONLY,
            ResultSet.CONCUR_READ_ONLY);
    statement.executeUpdate();
    return true;
} catch (SQLException e) {
    LOG.error("Error ", e);
    return false;
}
