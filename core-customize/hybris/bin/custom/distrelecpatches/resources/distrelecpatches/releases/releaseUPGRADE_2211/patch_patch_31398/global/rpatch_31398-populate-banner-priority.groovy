package distrelecpatches.releases.releaseUPGRADE_2211.patch_patch_31398.global

import de.hybris.platform.core.Registry
import de.hybris.platform.util.Config
import java.sql.Connection
import java.sql.Statement
import org.slf4j.Logger
import org.slf4j.LoggerFactory

Logger LOG = LoggerFactory.getLogger("Populate_banner_priority_script")

String query
if (Config.isSQLServerUsed()) {
    query = "UPDATE cc SET cc.p_priority = '0' FROM cmscomponent cc"
} else {
    query = "UPDATE cmscomponent cc SET cc.p_priority = '0'"
}

query += """ WHERE cc.p_priority is null
  and exists(select ct.pk from composedtypes ct where cc.typepkstring=ct.pk
    and exists(select sct.pk from composedtypes sct where ct.supertypepk=sct.pk and sct.internalcode in ('AbstractBannerComponent', 'BannerComponent')))"""

Connection connection = Registry.getCurrentTenant().getDataSource().getConnection()
Statement statement = connection.prepareStatement(query)
try {
    updateRows = statement.executeUpdate()
    connection.commit()
} finally {
    statement.close()
}

String msg = "Updated " + updateRows + " rows"
LOG.info(msg)
return msg
