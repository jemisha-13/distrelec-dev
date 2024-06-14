import de.hybris.platform.core.Registry
import de.hybris.platform.util.Config
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

@Field Logger LOG = LoggerFactory.getLogger("Processes_Cleanup")

@Field int batchSize = 1000

@Field Connection connection = Registry.getCurrentTenant().getDataSource().getConnection()

def getLimitQuery() {
    if (Config.isHanaUsed()) {
        return "LIMIT $batchSize"
    } else {
        return "AND rownum <= $batchSize"
    }
}

List<Long> getOldProcessesPks() {
    String getOldProcessesQuery = "SELECT pk FROM processes WHERE modifiedts < ? ${getLimitQuery()}"
    List<Long> oldProcessesPks = new ArrayList<>();
    PreparedStatement getOldProcessesStatement = connection.prepareStatement(getOldProcessesQuery);
    try {
        Calendar cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -1)
        Date previousYear = cal.getTime()
        getOldProcessesStatement.setDate(1, convertUtilToSql(previousYear))
        ResultSet rs = getOldProcessesStatement.executeQuery()

        while (rs.next()) {
            Long pk = rs.getLong(1)
            oldProcessesPks.add(pk)
        }
    } finally {
        getOldProcessesStatement.close()
    }
    return oldProcessesPks
}

def cleanTaskLogs(List<Long> oldProcessesPks) {
    String cleanTaskLogsQuery = "delete from tasklogs where p_process in (?"
    for (int i = 1 ; i < oldProcessesPks.size(); i++) {
        cleanTaskLogsQuery += ",?"
    }
    cleanTaskLogsQuery += ")"

    PreparedStatement cleanTaskLogsStatement = connection.prepareStatement(cleanTaskLogsQuery)
    try {
        int i = 1;
        for (Long oldProcessPk : oldProcessesPks) {
            cleanTaskLogsStatement.setLong(i++, oldProcessPk);
        }
        cleanTaskLogsStatement.executeUpdate()
        connection.commit()
    } finally {
        cleanTaskLogsStatement.close()
    }
}

def cleanProcesses(List<Long> oldProcessesPks) {
    String cleanProcessesQuery = "delete from processes where pk in (?"
    for (int i = 1 ; i < oldProcessesPks.size(); i++) {
        cleanProcessesQuery += ",?"
    }
    cleanProcessesQuery += ")"

    PreparedStatement cleanProcessesStatement = connection.prepareStatement(cleanProcessesQuery)
    try {
        int i = 1;
        for (Long oldProcessPk : oldProcessesPks) {
            cleanProcessesStatement.setLong(i++, oldProcessPk);
        }
        cleanProcessesStatement.executeUpdate()
        connection.commit()
    } finally {
        cleanProcessesStatement.close()
    }
}

java.sql.Date convertUtilToSql(Date uDate) {
    java.sql.Date sDate = new java.sql.Date(uDate.getTime())
    return sDate
}

List<Long> oldProcessesPks = getOldProcessesPks()
while (!oldProcessesPks.isEmpty()) {
    cleanTaskLogs(oldProcessesPks)
    cleanProcesses(oldProcessesPks)
    LOG.info("Cleaned " + oldProcessesPks.size() + " records")
    oldProcessesPks = getOldProcessesPks()
}


return "Processes cleaned"


