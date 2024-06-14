import com.distrelec.b2b.core.search.data.PimWebUseField
import com.google.gson.Gson
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum
import de.hybris.platform.catalog.enums.ClassificationAttributeVisibilityEnum
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel
import de.hybris.platform.classification.ClassificationService
import de.hybris.platform.classification.features.Feature
import de.hybris.platform.classification.features.FeatureList
import de.hybris.platform.classification.features.FeatureValue
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.servicelayer.i18n.CommonI18NService
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.session.SessionExecutionBody
import de.hybris.platform.servicelayer.session.SessionService
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes
import org.apache.commons.collections.CollectionUtils
import org.apache.logging.log4j.LogManager

Gson GSON = new Gson();

LOG = LogManager.getLogger("PimWebUseNonLocalized.groovy")


ClassificationService classificationService = spring.getBean("classificationService")
FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class)


ModelService modelService = spring.getBean("modelService")


//String productQuery = "select {pk} from {Product} where {pimWebUseJson[en]} is null and {pimwebuse[en]} is not null  and  rownum <= 10000"
//String productQuery = "select {pk} from {Product} where  {pimwebuse[en]} is not null"

//String productQuery = "select {pk} from {product} where p_pimwebusenonlocalized is not null and {pimwebusejson[en]} is null and rownum <= 200"
//String productQuery = "select {pk} from {product} order by {code} desc OFFSET 14000 ROWS FETCH NEXT 6000 ROWS ONLY"
//String productQuery = "select {pk} from {product} where rownum < 20000 order by {code} "
String productQuery = "select {pk} from {product} where {code} = '16054757'"

SessionService sessionService = spring.getBean("sessionService")
CommonI18NService commonI18NService = spring.getBean("commonI18NService")

List<ProductModel> products = flexibleSearchService.search(productQuery).getResult()

Integer totalProducts = products.size()

//select distinct {l.isocode} from {BaseStore2LanguageRel as lr join language as l on {l.pk}={lr.target}}
def languages = List.of("en", "sv")

LOG.info("Start processing $totalProducts products")
products.eachWithIndex { product, index ->

  languages.each { lang ->

	sessionService.executeInLocalView(new SessionExecutionBody() {

	  @Override
	  void executeWithoutResult()
	  {
		def langModel = commonI18NService.getLanguage(lang);
		commonI18NService.setCurrentLanguage(langModel)
		createPimWebUseForLanguage(classificationService, product, GSON, modelService, index, totalProducts, lang)
	  }
	})
  }

}

private void createPimWebUseForLanguage(ClassificationService classificationService, ProductModel product, Gson GSON, ModelService modelService, int index, int totalProducts, String lang)
{

  def millis = System.currentTimeMillis()

  FeatureList features = classificationService.getFeatures(product)

  List<PimWebUseField> fields = new ArrayList<>();


  for (final Feature feature : features)
  {
	
	if (CollectionUtils.isEmpty(feature.getValues()))
	{
	  continue
	}

	ClassAttributeAssignmentModel classAssign = feature.getClassAttributeAssignment();


	if (!isPimWebUseVisible(classAssign) || isPimWebUseDfeature(classAssign))
	{
	  continue
	}

	
	if (classAssign == null || classAssign.getClassificationAttribute() == null)
	{
	  continue
	}

	String code = classAssign.getClassificationAttribute().getCode();
	String name = classAssign.getClassificationAttribute().getName() //locale here
	
	 
	classAssign.getClassificationAttribute()

	for (FeatureValue value : feature.values)
	{
	  
	  String unitSymbol = value.getUnit() != null ? value.getUnit().getSymbol() : ""
	  
	  
	  LOG.info(value.class.getName())

	  PimWebUseField pimWebUse = new PimWebUseField()
	  pimWebUse.value = value.value
	  pimWebUse.code = code
	  pimWebUse.attributeName = name
	  pimWebUse.unit = unitSymbol


	  String fieldType = ClassificationAttributeTypeEnum.NUMBER == classAssign.getAttributeType() ?
			  SolrPropertiesTypes.DOUBLE.getCode() : SolrPropertiesTypes.STRING.getCode();
	  pimWebUse.setFieldType(fieldType);

	  fields.add(pimWebUse)
	}

  }
  if (CollectionUtils.isNotEmpty(fields))
  {
	def json = GSON.toJson(fields)
	product.setPimWebUseJson(json)//locale here
	modelService.save(product)
	println(json)
	LOG.info("PimWebUseJson updated for product: ${product.getCode()}[$lang], progress: $index/$totalProducts")
	
//	println("PimWebUseJson updated for product: ${product.getCode()} processed: $index/$totalProducts")

	println("time take:" + (System.currentTimeMillis() - millis))
  }
  else
  {
//	println("pim-webuse-field ws empty")
	LOG.info("PimWebUseJson not updated for product[$lang]:${product.getCode()}, progress: $index/$totalProducts")
  }



}


protected boolean isPimWebUseDfeature(final ClassAttributeAssignmentModel assignment)
{
  return assignment != null && (assignment.getVisibility() == ClassificationAttributeVisibilityEnum.D_VISIBILITY);
}

protected static boolean isPimWebUseVisible(final ClassAttributeAssignmentModel assignment)
{
  return assignment != null && (assignment.getVisibility() == ClassificationAttributeVisibilityEnum.A_VISIBILITY //
		  || assignment.getVisibility() == ClassificationAttributeVisibilityEnum.B_VISIBILITY //
		  || assignment.getVisibility() == ClassificationAttributeVisibilityEnum.C_VISIBILITY //
		  || assignment.getVisibility() == ClassificationAttributeVisibilityEnum.D_VISIBILITY);
}







