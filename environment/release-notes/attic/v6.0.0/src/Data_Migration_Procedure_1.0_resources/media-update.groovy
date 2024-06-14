import com.google.common.hash.Hashing;
import java.util.logging.Logger

def logger = Logger.getLogger("")
logger.info ("This script will perform locationHash update for all medias.\n")

def salt = "35b5cd0da3121fc53b4bc84d0c8af2e81"  

def conn = de.hybris.platform.core.Registry.currentTenant.dataSource.connection
conn.setAutoCommit(false);
def queryStm = conn.createStatement()
queryStm.setFetchSize(10_000)
def query = "SELECT PK, P_SUBFOLDERPATH, P_DATAPK, REALFILENAME FROM medias WHERE P_LOCATION IS NULL AND REALFILENAME IS NOT NULL AND P_SUBFOLDERPATH IS NOT NULL AND P_DATAPK IS NOT NULL"
def rs = queryStm.executeQuery(query);
logger.info("Fetch successful")

def ps = conn.prepareStatement("UPDATE medias SET P_LOCATION = ? , P_INTERNALURL='replicated273654712', P_LOCATIONHASH = ? WHERE PK = ?")

def pk, location, updatedLocationHash
def updated = 0 

while(rs.next()){
	pk = rs.getLong("PK")
	realFileName = rs.getString("REALFILENAME");
    location = rs.getString("P_SUBFOLDERPATH") + rs.getString("P_DATAPK") + (realFileName.lastIndexOf('.') >=0 ? realFileName.substring(realFileName.lastIndexOf('.')): "");
  	updatedLocationHash = mediaLocationHashService.createHashForLocation('root', location);
    ps.setString(1, location);
    ps.setString(2, updatedLocationHash);
    ps.setLong(3, pk)
    ps.addBatch()
	updated++ 	
	if(updated % 10000 == 0){
		ps.executeBatch()
		ps.clearBatch()
		logger.info( "updated: " + updated)
	}
}
ps.executeBatch()
conn.commit()

rs.close()
queryStm.close()
conn.close()

logger.info( "All medias updated successfuly. Number of items updated: " + updated)
return "All medias updated successfuly. Number of items updated: " + updated
