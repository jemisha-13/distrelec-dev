package distrelecpatches.releases.releaseUPGRADE_2211.patch_patch_31849.global

import de.hybris.platform.core.Registry
import java.sql.Connection
import java.sql.Statement
import org.slf4j.Logger
import org.slf4j.LoggerFactory

Logger LOG = LoggerFactory.getLogger("Reset-cronjob-nodeid")

query = "update cronjobs set p_nodeid = null where p_nodeid is not null"

Connection connection = Registry.getCurrentTenant().getDataSource().getConnection()
Statement statement = connection.prepareStatement(query)
try {
    updateRows = statement.executeUpdate()
} finally {
    statement.close()
}

String msg = "Updated " + updateRows + " rows"
LOG.info(msg)
return msg
