import de.hybris.platform.core.Registry;
import de.hybris.platform.persistence.property.DBPersistenceManager;

((DBPersistenceManager) Registry.getPersistenceManager()).reloadPersistenceInfos();
Registry.getCurrentTenant().getCache().clear();