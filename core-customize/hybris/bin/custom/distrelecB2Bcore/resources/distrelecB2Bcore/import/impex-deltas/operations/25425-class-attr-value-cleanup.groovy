import de.hybris.platform.core.Registry
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.PreparedStatement

@Field Logger LOG = LoggerFactory.getLogger("ClassificationAttributeValue_Cleanup")

@Field int cleanedRecords = 0

@Field Connection connection = Registry.getCurrentTenant().getDataSource().getConnection()

def deleteDuplicateAttributeValueAssignmentRecords() {
    LOG.info("Delete duplicate AttributeValueAssignment records")

    String deleteQuery = "DELETE FROM attr2valuerel item_t0 WHERE (exists (" +
            "SELECT  item_t1.PK  FROM classattrvalues item_t1 WHERE ( item_t0.p_value = item_t1.PK  and exists (" +
            "SELECT trim( item_t2.p_code ),count(*) FROM classattrvalues item_t2 WHERE (trim( item_t1.p_code )=trim( item_t2.p_code )) GROUP BY trim( item_t2.p_code ) having count(*) > 1) " +
            "and item_t1.PK  <> (SELECT min( item_t3.PK ) FROM classattrvalues item_t3 WHERE (trim( item_t1.p_code )=trim( item_t3.p_code ))))))"

    PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)
    try {
        cleanedRecords += deleteStatement.executeUpdate()
        connection.commit()
    } finally {
        deleteStatement.close()
    }
}
def deleteDuplicateClassificationAttributeValueLpRecords() {
    LOG.info("Delete duplicate ClassificationAttributeValueLP records")

    String deleteQuery = "DELETE FROM classattrvalueslp cavlp where (exists(select * FROM classattrvalues item_t0 WHERE cavlp.itempk=item_t0.pk and (exists (" +
            "SELECT trim( item_t1.p_code ),count(*) FROM classattrvalues item_t1 WHERE (trim( item_t0.p_code )=trim( item_t1.p_code )) GROUP BY trim( item_t1.p_code ) having count(*) > 1)" +
            " and item_t0.PK  <> (SELECT min( item_t2.PK ) FROM classattrvalues item_t2 WHERE (trim( item_t0.p_code )=trim( item_t2.p_code )) ))))"

    PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)
    try {
        cleanedRecords += deleteStatement.executeUpdate()
        connection.commit()
    } finally {
        deleteStatement.close()
    }
}

def deleteDuplicateClassificationAttributeValueRecords() {
    LOG.info("Delete duplicate ClassificationAttributeValue records")

    String deleteQuery = "DELETE FROM classattrvalues item_t0 WHERE (exists (" +
            "SELECT trim( item_t1.p_code ),count(*) FROM classattrvalues item_t1 WHERE (trim( item_t0.p_code )=trim( item_t1.p_code )) GROUP BY trim( item_t1.p_code ) having count(*) > 1)" +
            " and item_t0.PK  <> (SELECT min( item_t2.PK ) FROM classattrvalues item_t2 WHERE (trim( item_t0.p_code )=trim( item_t2.p_code )) ))"

    PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)
    try {
        cleanedRecords += deleteStatement.executeUpdate()
        connection.commit()
    } finally {
        deleteStatement.close()
    }
}

def removeLeadingZeros() {
    LOG.info("Remove leading spaces from ClassificationAttributeValue")

    String removeLeadingSpacesQuery = "UPDATE classattrvalues SET p_code=trim(p_code)"

    PreparedStatement removeLeadingSpacesStatement = connection.prepareStatement(removeLeadingSpacesQuery)
    try {
        cleanedRecords += removeLeadingSpacesStatement.executeUpdate()
        connection.commit()
    } finally {
        removeLeadingSpacesStatement.close()
    }
}

def clearHybrisCache() {
    LOG.info("Clearing cache")
    Registry.getCurrentTenant().getCache().clear();
    LOG.info("Cache was cleared")
}

deleteDuplicateAttributeValueAssignmentRecords()
deleteDuplicateClassificationAttributeValueLpRecords()
deleteDuplicateClassificationAttributeValueRecords()
removeLeadingZeros()
clearHybrisCache()

return "Finished. Total cleaned rows: " + cleanedRecords
