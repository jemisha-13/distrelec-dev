import de.hybris.platform.core.Registry
import de.hybris.platform.util.Config
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

@Field Logger LOG = LoggerFactory.getLogger("SavedValues_Cleanup")

@Field int batchSize = 1000

@Field Connection connection = Registry.getCurrentTenant().getDataSource().getConnection()

def getLimitQuery() {
    if (Config.isHanaUsed()) {
        return "LIMIT $batchSize"
    } else {
        return "AND rownum <= $batchSize"
    }
}

List<Long> getOldSavedValuesPks() {
    String getOldSavedValuesQuery = "SELECT pk FROM savedvalues WHERE modifiedts < ? ${getLimitQuery()}"
    List<Long> oldSavedValuesPks = new ArrayList<>();
    PreparedStatement getOldSavedValuesStatement = connection.prepareStatement(getOldSavedValuesQuery);
    try {
        Calendar cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -1)
        Date previousYear = cal.getTime()
        getOldSavedValuesStatement.setDate(1, convertUtilToSql(previousYear))
        ResultSet rs = getOldSavedValuesStatement.executeQuery()

        while (rs.next()) {
            Long pk = rs.getLong(1)
            oldSavedValuesPks.add(pk)
        }
    } finally {
        getOldSavedValuesStatement.close()
    }
    return oldSavedValuesPks
}

def cleanPropsSavedValues(List<Long> oldSavedValuesPks) {
    String cleanPropsSVQuery = "delete from props where itempk in (?"

    for (int i = 1 ; i < oldSavedValuesPks.size(); i++) {
        cleanPropsSVQuery += ",?"
    }
    cleanPropsSVQuery += ")"

    PreparedStatement cleanPropsSVStatement = connection.prepareStatement(cleanPropsSVQuery);
    try {
        int i = 1;
        for (Long oldSavedValuesPk : oldSavedValuesPks) {
            cleanPropsSVStatement.setLong(i++, oldSavedValuesPk);
        }
        cleanPropsSVStatement.executeUpdate()
        connection.commit()
    } finally {
        cleanPropsSVStatement.close()
    }

}

def cleanPropsSavedValuesEntries(List<Long> oldSavedValuesPks) {
    String cleanPropsSVEQuery = "delete from props where exists(select 1 from savedvalueentry sve where sve.pk=itempk and sve.p_parent in (?"

    for (int i = 1 ; i < oldSavedValuesPks.size(); i++) {
        cleanPropsSVEQuery += ",?"
    }
    cleanPropsSVEQuery += "))"

    PreparedStatement cleanPropsSVEStatement = connection.prepareStatement(cleanPropsSVEQuery);
    try {
        int i = 1;
        for (Long oldSavedValuesPk : oldSavedValuesPks) {
            cleanPropsSVEStatement.setLong(i++, oldSavedValuesPk);
        }
        cleanPropsSVEStatement.executeUpdate()
        connection.commit()
    } finally {
        cleanPropsSVEStatement.close()
    }

}

def cleanSavedValueEntries(List<Long> oldSavedValuesPks) {
    String cleanSavedValueEntriesQuery = "delete from savedvalueentry where p_parent in (?"
    for (int i = 1 ; i < oldSavedValuesPks.size(); i++) {
        cleanSavedValueEntriesQuery += ",?"
    }
    cleanSavedValueEntriesQuery += ")"

    PreparedStatement cleanSavedValueEntriesStatement = connection.prepareStatement(cleanSavedValueEntriesQuery)
    try {
        int i = 1;
        for (Long oldSavedValuesPk : oldSavedValuesPks) {
            cleanSavedValueEntriesStatement.setLong(i++, oldSavedValuesPk);
        }
        cleanSavedValueEntriesStatement.executeUpdate()
        connection.commit()
    } finally {
        cleanSavedValueEntriesStatement.close()
    }
}

def cleanSavedValues(List<Long> oldSavedValuesPks) {
    String cleanSavedValuesQuery = "delete from savedvalues where pk in (?"
    for (int i = 1 ; i < oldSavedValuesPks.size(); i++) {
        cleanSavedValuesQuery += ",?"
    }
    cleanSavedValuesQuery += ")"

    PreparedStatement cleanSavedValuesStatement = connection.prepareStatement(cleanSavedValuesQuery)
    try {
        int i = 1;
        for (Long oldSavedValuesPk : oldSavedValuesPks) {
            cleanSavedValuesStatement.setLong(i++, oldSavedValuesPk);
        }
        cleanSavedValuesStatement.executeUpdate()
        connection.commit()
    } finally {
        cleanSavedValuesStatement.close()
    }
}

java.sql.Date convertUtilToSql(Date uDate) {
    java.sql.Date sDate = new java.sql.Date(uDate.getTime())
    return sDate
}

List<Long> oldSavedValuesPks = getOldSavedValuesPks()
while (!oldSavedValuesPks.isEmpty()) {
    cleanPropsSavedValues(oldSavedValuesPks)
    cleanPropsSavedValuesEntries(oldSavedValuesPks)
    cleanSavedValueEntries(oldSavedValuesPks)
    cleanSavedValues(oldSavedValuesPks)
    LOG.info("Cleaned " + oldSavedValuesPks.size() + " records")
    oldSavedValuesPks = getOldSavedValuesPks()
}


return "Saved values cleaned"


