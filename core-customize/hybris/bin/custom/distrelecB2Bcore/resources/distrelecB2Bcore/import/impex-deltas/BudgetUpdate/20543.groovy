import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;
import java.util.logging.Logger;
import groovy.transform.Field;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.util.StandardDateRange;
import de.hybris.platform.servicelayer.model.ModelService;
import java.io.PrintWriter;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
@Field
static final Logger LOG = Logger.getLogger("20543-groovy")

@Field
FlexibleSearchService searchService = spring.getBean(FlexibleSearchService.class)

@Field
ModelService modelService = spring.getBean(ModelService.class)

@Field
ConfigurationService configurationService = spring.getBean(ConfigurationService.class)

try {
      main()
     return "FINISHED";
} catch (Exception e){
    println("Exception occurred: stacktrace :" + e.printStackTrace())
    return "ERROR"

}

def main(){
  
  LOG.info("Starting Updating Budgets Cron job")
  List<DistB2BBudgetModel> budgetModels=getallBudgets();
  try  {
	 final Configuration configuration =  configurationService.getConfiguration();
	  final String filePath = configuration.getString("HYBRIS_TEMP_DIR");
	 PrintWriter writer = new PrintWriter(new File(filePath+File.separator+"BudgetExport.csv"));
	 StringBuilder header = new StringBuilder();
	 header.append("code");
      header.append(',');
      header.append("Start Date");
	  header.append(',');
      header.append("End Date");
      header.append('\n');
	  writer.write(header.toString());
	 for(final DistB2BBudgetModel budget : budgetModels) {
      StringBuilder sb = new StringBuilder();
	
      sb.append(budget.getCode());
      sb.append(',');
      sb.append(budget.getDateRange().getStart());
	  sb.append(',');
      sb.append(budget.getDateRange().getEnd());
      sb.append('\n');
	  writer.write(sb.toString());

      sb=null;
	  }
	  
	  for(final DistB2BBudgetModel budget : budgetModels) {
			final Calendar budgetEndDate = Calendar.getInstance();
			StandardDateRange dateRange=budget.getDateRange();
			LOG.info("Before Update Start Date is ::"+dateRange.getStart())
			LOG.info("Before Update End Date is ::"+dateRange.getEnd())
			budgetEndDate.setTime(dateRange.getEnd());
			budgetEndDate.add(Calendar.YEAR,100);
			StandardDateRange newrange=new StandardDateRange(dateRange.getStart(), budgetEndDate.getTime());
			budget.setDateRange(newrange);
			LOG.info("After Update Start Date is ::"+newrange.getStart())
			LOG.info("After Update End Date is ::"+newrange.getEnd())
			modelService.save(budget);
		}
		
    } catch (FileNotFoundException e) {
      LOG.info(e.getMessage());
    }finally{
		writer.close();
	}
  
}

List<DistB2BBudgetModel> getallBudgets(){
  LOG.info("Getting All Budgets")
  String topLevelQuery = """SELECT {PK} FROM {DistB2BBudget}"""
  return searchService.search(topLevelQuery).result as List<DistB2BBudgetModel>
}
