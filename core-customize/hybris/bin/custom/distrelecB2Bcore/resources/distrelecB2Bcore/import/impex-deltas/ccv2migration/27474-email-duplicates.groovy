import de.hybris.platform.core.Registry
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.PreparedStatement

@Field Logger LOG = LoggerFactory.getLogger("Email_Cleanup")

@Field Connection connection = Registry.getCurrentTenant().getDataSource().getConnection()

def changeEmailDisplayName(String displayName, long pk) {
    String changeEmailDisplayNameQuery = "UPDATE emailaddress SET p_displayname=? WHERE pk=?"

    PreparedStatement changeEmailDisplayNameStatement = connection.prepareStatement(changeEmailDisplayNameQuery);
    try {
        changeEmailDisplayNameStatement.setString(1, displayName);
        changeEmailDisplayNameStatement.setLong(2, pk);
        changeEmailDisplayNameStatement.executeUpdate()
        connection.commit();
    } finally {
        changeEmailDisplayNameStatement.close()
    }
}

def clearHybrisCache() {
    LOG.info("Clearing cache")
    Registry.getCurrentTenant().getCache().clear();
    LOG.info("Cache was cleared")
}

changeEmailDisplayName("Göran Sandin", 8853011503167L);
changeEmailDisplayName("Göran Sandin ",8852914182207L);
changeEmailDisplayName("Jon Ivar Skjærpe ",8856359245887L);
changeEmailDisplayName("Jon Ivar Skjærpe",8856296298559L);
changeEmailDisplayName("Norbert Bussmann",8819410278463L);
changeEmailDisplayName("Norbert Bussmann ",8819373938751L);
changeEmailDisplayName("Kalervo Hyytiä ",8841772079167L);
changeEmailDisplayName("Kalervo Hyytiä",8853275187263L);
changeEmailDisplayName("JürgenKausch",8836757002303L);
changeEmailDisplayName("JürgenKausch ",8853209880639L);
changeEmailDisplayName("JürgSchnidrig",8848654112831L);
changeEmailDisplayName("JürgSchnidrig ",8853011601471L);
changeEmailDisplayName("Dan Staerk ",8864149968959L);
changeEmailDisplayName("Dan Staerk",8864725407807L);
changeEmailDisplayName("Niclas Sjölander.",8843294087231L);
changeEmailDisplayName("Niclas Sjölander. ",8864437311551L);
changeEmailDisplayName("Teppo Nissilä",8853176326207L);
changeEmailDisplayName("Teppo Nissilä ",8858352588863L);
changeEmailDisplayName("TündeBaló ",8853078186047L);
changeEmailDisplayName("TündeBaló",8857079289919L);
changeEmailDisplayName("PärBertilsson",8821445007423L);
changeEmailDisplayName("PärBertilsson ",8853208635455L);
changeEmailDisplayName("Sven Tress",8815922387007L);
changeEmailDisplayName("Sven Tress ",8815951910975L);

clearHybrisCache();

return "Finished."
