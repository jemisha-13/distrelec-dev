import de.hybris.platform.core.Registry
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

@Field Logger LOG = LoggerFactory.getLogger("Email_Cleanup")

@Field int cleanedRecords = 0
@Field int batchSize = 100

@Field Connection connection = Registry.getCurrentTenant().getDataSource().getConnection()

def trimOneEmailAddressInGroup() {
    LOG.info("Get email addresses for trimming")

    String getEmailQuery = "SELECT email.pk" +
            " FROM emailaddress email" +
            " WHERE (email.p_displayname<>TRIM(email.p_displayname) OR email.p_emailaddress<>TRIM(email.p_emailaddress))" +
            " AND EXISTS(" +
            "   SELECT * FROM (" +
            "     SELECT email2.pk, RANK() OVER (PARTITION BY TRIM(email2.p_displayname), TRIM(email2.p_emailaddress) ORDER BY email2.pk ASC) AS pkRank" +
            "     FROM emailaddress email2) pkRanks" +
            "   WHERE pkRanks.pkRank=1 and pkRanks.pk=email.pk)" +
            " AND NOT EXISTS (" +
            "   SELECT *" +
            "   FROM emailaddress email2" +
            "   WHERE TRIM(email.p_displayname)=TRIM(email2.p_displayname) AND TRIM(email.p_emailaddress)=TRIM(email2.p_emailaddress) AND" +
            "    email2.p_displayname=TRIM(email2.p_displayname) AND email2.p_emailaddress=TRIM(email2.p_emailaddress))"

    PreparedStatement getEmailStatement = connection.prepareStatement(getEmailQuery)
    List<Long> pksToTrim = new ArrayList<>()
    try {
        ResultSet rs = getEmailStatement.executeQuery()
        while (rs.next()) {
            Long pk = rs.getLong(1)
            pksToTrim.add(pk)
        }
        connection.commit()
    } finally {
        getEmailStatement.close()
    }

    int i = 0
    while (i < pksToTrim.size()) {
        LOG.info("Trim email addresses starting from " + i)
        String trimEmailQuery = "UPDATE emailaddress SET p_displayname=TRIM(p_displayname), p_emailaddress=TRIM(p_emailaddress) WHERE pk IN (?"


        for (int j = 1; i + j < pksToTrim.size() && j < batchSize; j++) {
            trimEmailQuery += ",?"
        }
        trimEmailQuery += ")"

        PreparedStatement trimEmailStatement = connection.prepareStatement(trimEmailQuery);
        try {
            for (int j = 0; i + j < pksToTrim.size() && j < batchSize; j++) {
                trimEmailStatement.setLong(j + 1, pksToTrim.get(i + j));
            }
            cleanedRecords += trimEmailStatement.executeUpdate()
            connection.commit()
        } finally {
            trimEmailStatement.close()
        }
        i += batchSize
    }
}

def relinkEmails(String table, String field) {
    LOG.info("Relink emails in table " +  table + " field " + field)
    String selectRelinkEmailsQuery = selectRelinkEmailsQuery(table, field);

    PreparedStatement selectRelinkEmailStatement = connection.prepareStatement(selectRelinkEmailsQuery)
    List<Long> pksToRelink = new ArrayList<>()
    try {
        ResultSet rs = selectRelinkEmailStatement.executeQuery()
        while (rs.next()) {
            Long pk = rs.getLong(1)
            pksToRelink.add(pk)
        }
        connection.commit()
    } finally {
        selectRelinkEmailStatement.close()
    }

    int i = 0
    while (i < pksToRelink.size()) {
        LOG.info("Trim emails in table " +  table + " field " + field + " starting from " + i)

        String relinkEmailsQuery = relinkEmailsQuery(table, field, Integer.min(batchSize, pksToRelink.size() - i))

        PreparedStatement relinkEmailsStatement = connection.prepareStatement(relinkEmailsQuery)

        try {
            for (int j = 0; i + j < pksToRelink.size() && j < batchSize; j++) {
                relinkEmailsStatement.setLong(j + 1, pksToRelink.get(i + j))
            }
            cleanedRecords += relinkEmailsStatement.executeUpdate()
            connection.commit()
        } finally {
            relinkEmailsStatement.close()
        }

        i += batchSize
    }
}

String selectRelinkEmailsQuery(String table, String field) {
    return "SELECT rel.pk" +
            "  FROM " + table + " rel" +
            "  JOIN emailaddress email ON rel." + field + "=email.pk" +
            "  WHERE (email.p_displayname<>TRIM(email.p_displayname) OR email.p_emailaddress<>TRIM(email.p_emailaddress))"
}

String relinkEmailsQuery(String table, String field, int batchSize) {
    String query = "UPDATE " + table + " rel SET rel." + field + "=(" +
            "    SELECT email2.pk " +
            "    FROM emailaddress email " +
            "    JOIN emailaddress email2 ON TRIM(email.p_displayname)=email2.p_displayname AND TRIM(email.p_emailaddress)=email2.p_emailaddress" +
            "    WHERE rel." + field + "=email.pk " +
            "    ) " +
            " WHERE rel.pk IN (?"

    for (int i = 1; i < batchSize; i++) {
        query += ",?"
    }
    query += ")"
    return query;
}

String dropUntrimmedEmails() {
    LOG.info("Drop untrimmed email addresses")

    String dropUntrimmedEmailQuery = "DELETE" +
            " FROM emailaddress email" +
            " WHERE email.p_displayname<>TRIM(email.p_displayname) OR email.p_emailaddress<>TRIM(email.p_emailaddress)"

    PreparedStatement dropUntrimmedEmailStatement = connection.prepareStatement(dropUntrimmedEmailQuery)
    try {
        cleanedRecords += dropUntrimmedEmailStatement.executeUpdate()
        connection.commit()
    } finally {
        dropUntrimmedEmailStatement.close()
    }
}


def clearHybrisCache() {
    LOG.info("Clearing cache")
    Registry.getCurrentTenant().getCache().clear();
    LOG.info("Cache was cleared")
}

trimOneEmailAddressInGroup()
relinkEmails("EmailMsg2ToAddrRel", "targetpk")
relinkEmails("EmailMsg2BccAddrRel", "targetpk")
relinkEmails("emailmessage", "p_fromaddress")
dropUntrimmedEmails()
clearHybrisCache()

return "Finished. Total cleaned rows: " + cleanedRecords
