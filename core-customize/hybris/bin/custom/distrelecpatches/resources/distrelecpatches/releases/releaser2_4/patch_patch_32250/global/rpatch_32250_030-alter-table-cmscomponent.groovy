package distrelecpatches.releases.releaser2_4.patch_patch_32250.global

import de.hybris.platform.core.Registry
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.Statement

@Field Logger LOG = LoggerFactory.getLogger("rpatch_32250_030-alter-table-cmscomponent.groovy")

def dropDbColumn(String query) {
    Connection connection = Registry.getCurrentTenant().getDataSource().getConnection()
    Statement statement = connection.prepareStatement(query)
    try {
        updateRows = statement.executeUpdate()
        connection.commit()
    } finally {
        statement.close()
    }

    String msg = "Dropped " + updateRows + " columns"
    LOG.info(msg)
    return msg
}

dropDbColumn("ALTER TABLE cmscomponent DROP COLUMN p_limit")
dropDbColumn("ALTER TABLE cmscomponent DROP COLUMN p_style")
