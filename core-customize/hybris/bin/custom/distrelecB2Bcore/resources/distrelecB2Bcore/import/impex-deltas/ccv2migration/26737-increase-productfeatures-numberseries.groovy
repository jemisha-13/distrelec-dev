import de.hybris.platform.core.Registry;
import de.hybris.platform.core.DefaultPKCounterGenerator;
import de.hybris.platform.persistence.numberseries.SerialNumberGenerator;

int key = 611; // product features
String seriesKey = "pk_" + key;
long current = new DefaultPKCounterGenerator().fetchNextCounter(key);
SerialNumberGenerator generator = Registry.getCurrentTenant().getSerialNumberGenerator();
generator.removeSeries(seriesKey);
generator.createSeries(seriesKey,1, current * 6);
