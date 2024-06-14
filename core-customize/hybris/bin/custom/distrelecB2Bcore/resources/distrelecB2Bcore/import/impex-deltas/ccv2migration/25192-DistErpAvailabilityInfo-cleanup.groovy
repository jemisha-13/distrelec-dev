import com.sap.db.jdbc.exceptions.JDBCDriverException
import de.hybris.platform.core.Registry
import de.hybris.platform.util.Config
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

@Field Logger LOG = LoggerFactory.getLogger("DistErpAvailabilityInfo_Cleanup")

@Field int batchSize = 100

@Field Connection connection = Registry.getCurrentTenant().getDataSource().getConnection()


def createTempTable() {
    String query = "CREATE TABLE DISTERPAVAILABILITYINFOTMP ("
    query += "HJMPTS" + number()
    query += ",TYPEPKSTRING" + number()
    query += ",PK" + number()
    query += ",CREATEDTS TIMESTAMP"
    query += ",MODIFIEDTS TIMESTAMP"
    query += ",OWNERPKSTRING" + number()
    query += ",ACLTS" + number()
    query += ",PROPTS" + number()
    query += ",P_QUANTITY" + number()
    query += ",P_ESTIMATEDDELIVERYDATE TIMESTAMP"
    query += ",SEALED" + number()
    query += ")"

    PreparedStatement statement = connection.prepareStatement(query)
    try {
        statement.executeUpdate()
        connection.commit()
    } finally {
        statement.close()
    }
}

def dropTempTable() {
    String query = "DROP TABLE DISTERPAVAILABILITYINFOTMP"

    PreparedStatement statement = null;
    try {
        statement =  connection.prepareStatement(query);
        statement.executeUpdate()
        connection.commit()
    } catch (Exception e) {
        // ignore
    } finally {
        if (statement != null) {
            statement.close()
        }
    }
}

def number() {
    if (Config.isHanaUsed()) {
        return " BIGINT"
    } else {
        return " NUMBER(20)"
    }
}

List<Long> getErpAvailabilityInfoPks() {
    String getOrderEntriesQuery = "SELECT p_erpavailabilityinfos FROM orderentries WHERE p_erpavailabilityinfos <> ',#1,'"
    List<Long> erpAvailInfoPks = new ArrayList<>();
    PreparedStatement getOrderEntriesStatement = connection.prepareStatement(getOrderEntriesQuery);
    try {
        ResultSet rs = getOrderEntriesStatement.executeQuery()

        while (rs.next()) {
            String infos = rs.getString(1);
            String[] splittedInfos = infos.split(",")
            for (int i = 2; i < splittedInfos.length; i++) {
                erpAvailInfoPks.add(Long.parseLong(splittedInfos[i]))
            }
        }
    } finally {
        getOrderEntriesStatement.close()
    }
    return erpAvailInfoPks
}

def copyDataToTempTable(List<Long> erpAvailInfoPks) {
    String copyQuery = getCopyQuery(batchSize)
    for (int pkId = 0 ; pkId < erpAvailInfoPks.size() ; ) {
        int currentBatchSize = Math.min(batchSize, erpAvailInfoPks.size() - pkId)

        if (currentBatchSize < batchSize) {
            copyQuery = getCopyQuery(currentBatchSize)
        }

        PreparedStatement copyStatement = connection.prepareStatement(copyQuery)
        try {
            for (int batchId = 1; batchId <= currentBatchSize; batchId++) {
                copyStatement.setLong(batchId, erpAvailInfoPks[pkId++])
            }
            copyStatement.executeUpdate()
            connection.commit()
        } finally {
            copyStatement.close();
        }

        LOG.info("Copied ${pkId} records")
    }
}

String getCopyQuery(int currentBatchSize) {
    String copyQuery = "INSERT INTO DISTERPAVAILABILITYINFOTMP SELECT * FROM DISTERPAVAILABILITYINFO WHERE pk IN (";

    for (int i = 1 ; i < currentBatchSize ; i++) {
        copyQuery += "?,"
    }
    copyQuery += "?)"
    return copyQuery
}

def deleteOldRecords() {
    LOG.info("Delete old records")

    String deleteQuery = "TRUNCATE TABLE DISTERPAVAILABILITYINFO"

    PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)
    try {
        deleteStatement.executeUpdate()
        connection.commit()
    } finally {
        deleteStatement.close()
    }
}

def copyFromTempTable() {
    LOG.info("Copy records back from temp table")

    String copyQuery = "INSERT INTO DISTERPAVAILABILITYINFO SELECT * FROM DISTERPAVAILABILITYINFOTMP";
    PreparedStatement copyStatement = connection.prepareStatement(copyQuery)
    try {
        copyStatement.executeUpdate()
        connection.commit()
    } finally {
        copyStatement.close()
    }
}

createTempTable()
List<Long> erpAvailInfoPks = getErpAvailabilityInfoPks()
copyDataToTempTable(erpAvailInfoPks)
deleteOldRecords()
copyFromTempTable()
dropTempTable()

return "Finished. Total copied rows: " + erpAvailInfoPks.size()
