import de.hybris.platform.core.Registry
import groovy.transform.Field

import java.sql.Connection
import java.sql.PreparedStatement

@Field Connection connection = Registry.getCurrentTenant().getDataSource().getConnection()

String resetNodeIdsQuery = "update jobs set p_nodeid=null"

PreparedStatement resetNodeIdsStatement = connection.prepareStatement(resetNodeIdsQuery);
try {
    resetNodeIdsStatement.executeUpdate()
    connection.commit()
} finally {
    resetNodeIdsStatement.close()
}

return "Node ids were reset"
