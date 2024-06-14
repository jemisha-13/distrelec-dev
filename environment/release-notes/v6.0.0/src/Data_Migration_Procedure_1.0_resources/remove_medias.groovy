import com.google.common.hash.Hashing;
import java.util.logging.Logger

def logger = Logger.getLogger("")
logger.info ("This script will delete all medias from the product catalog.\n")


def conn = de.hybris.platform.core.Registry.currentTenant.dataSource.connection
conn.setAutoCommit(false);
def queryStm = conn.createStatement()
queryStm.setFetchSize(10_000)

// def query = "SELECT item_t0.PK FROM medias item_t0 where item_t0.p_catalogversion='8796093153881' AND item_t0.TYPEPKSTRING='8796094824530' AND item_t0.URL LIKE '/Web/WebShopImages%'"
def query = "SELECT item_t0.PK FROM medias item_t0 where item_t0.p_catalogversion='8796093153881'"


// SELECT item_t0.PK FROM medias item_t0 WHERE item_t0.p_catalogversion='8796093153881' AND item_t0.p_mediaContainer IS NULL

def rs = queryStm.executeQuery(query);
logger.info("Fetch successful")

def ps = conn.prepareStatement("DELETE FROM medias WHERE PK = ?")

removed = 0

while(rs.next()){
	pk = rs.getLong("PK")
    ps.setLong(1, pk)
    ps.addBatch()
	removed++ 	
	if(removed % 20000 == 0){
		ps.executeBatch()
		conn.commit()
		ps.clearBatch()
		logger.info("removed: " + removed)
	}
}
ps.executeBatch()
conn.commit()

rs.close()
queryStm.close()
conn.close()

logger.info( "All medias removed successfuly. Number of items removed: " + removed)
return "All medias removed successfuly. Number of items removed: " + removed
