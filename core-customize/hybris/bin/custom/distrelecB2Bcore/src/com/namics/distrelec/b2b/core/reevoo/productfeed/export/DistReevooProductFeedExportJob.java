package com.namics.distrelec.b2b.core.reevoo.productfeed.export;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.jobs.DistReevooProductFeedCronJobModel;
import com.namics.distrelec.b2b.core.model.reevoo.DistProductSendToReevooModel;
import com.namics.distrelec.b2b.core.reevoo.productfeed.dao.RevooProductFeedExportDao;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.media.MediaService;

public class DistReevooProductFeedExportJob extends AbstractJobPerformable<DistReevooProductFeedCronJobModel> {
	
	private static final Logger LOG = LogManager.getLogger(DistReevooProductFeedExportJob.class);
	
	private static final String[] HEADER = { "sku", "name", "product_category", "product_category2", "image_url",
    "manufacturer","mpn","ean","product_description" };
	 public static final int CATEGORY_SIZE_1 = 1;
	 public static final int CATEGORY_SIZE_2 = 2;
	 public static final int TEXT_START_INDEX = 0;
	 public static final int TEXT_MAX_INDEX = 191;
	 public static final int MANU_MAX_INDEX = 40;
	
	@Autowired
	RevooProductFeedExportDao revooProductFeedExportDao;
	
	@Autowired
	DistRevooCsvTransformationService distRevooCsvTransformationService;
	
	@Autowired
	DistReevooExportHelper reevooExportHelper;
	
	@Autowired
    private ConfigurationService configurationService;
	
	@Autowired
	MediaService mediaService;

	@Autowired
	private CatalogVersionService catalogVersionService;

	@Override
	public PerformResult perform(DistReevooProductFeedCronJobModel cronJob) {
		CronJobResult cronJobResult = CronJobResult.SUCCESS;
		CMSSiteModel site=cronJob.getSites().iterator().next();
		if(site.isReevooActivated()) {
			String url=getConfigurationService().getConfiguration().getString("website." + site.getUid() + (site.isHttpsOnly() ? ".https" : ".http"));
			try {
			List <ProductModel> eligibleProducts=revooProductFeedExportDao.getEligilbleProducts(site);
			List <ProductModel> exportedProducts=new ArrayList<ProductModel>();
			LOG.info("Cronjob Trigerred"+eligibleProducts.size());
			 final List<String[]> resultList = new ArrayList<String[]>();
			 for(ProductModel reevooProduct:eligibleProducts) {
				 String[] csvRow= new String[9];
				 try {
				 csvRow[0]=getMax191LengthString(reevooProduct.getCode());
				 csvRow[1]=getMax191LengthString(reevooProduct.getName());
				 final List<String> productCategoryInfo = new ArrayList<String>();
				  for (final CategoryModel category : reevooProduct.getSupercategories()) {
					  fillCategories(category,productCategoryInfo);
				  }
				 csvRow[2]=getMax191LengthString((CollectionUtils.isNotEmpty(productCategoryInfo) && productCategoryInfo.size() >= CATEGORY_SIZE_1)
		                    ? productCategoryInfo.get(productCategoryInfo.size() - CATEGORY_SIZE_1).toLowerCase()
		                            : DistConstants.Punctuation.EMPTY);
				 csvRow[3]=getMax191LengthString(null != reevooProduct.getPrimarySuperCategory() ? reevooProduct.getPrimarySuperCategory().getName(Locale.ENGLISH).toLowerCase() : DistConstants.Punctuation.EMPTY);
				 csvRow[4]=getImageUrl(reevooProduct,url);
				 csvRow[5]=getMax40LengthTextString(reevooProduct.getManufacturer()!=null ?reevooProduct.getManufacturer().getName():DistConstants.Punctuation.EMPTY);
				 csvRow[6]=getMax40LengthTextString(reevooProduct.getTypeName());
				 csvRow[7]=getMax40LengthTextString(reevooProduct.getEan());
				 csvRow[8]=getMax191LengthString(reevooProduct.getDescription());
				 resultList.add(csvRow);
				 exportedProducts.add(reevooProduct);
				 }catch(Exception ex) {
					 LOG.error("Could not add all data in Product Feed for Export"+reevooProduct.getCode(), ex);
				 }
				 
				 csvRow=null;
			 }
			 LOG.info("Export Trigerred for"+cronJob.getCode()+".Size of Export ::"+exportedProducts.size());
			 final InputStream exportData =getDistRevooCsvTransformationService().transform(HEADER, resultList);
			 	
		        try {
		        	reevooExportHelper.saveExportData(exportData, cronJob);
		        	LOG.info("Export Finished for"+cronJob.getCode());
		        	updateSendToReevooStatus(exportedProducts,site);
		        } catch (final Exception e) {
		            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
		        } finally {
		            IOUtils.closeQuietly(exportData);
		        }
			}catch(Exception ex) {
				LOG.error("Error in exporting Reevoo Product Feed Data for "+cronJob.getCode());
				cronJobResult = CronJobResult.ERROR;
			}
		}else {
			LOG.warn("Reevoo Product Feed not activated for Sales Org"+site.getSalesOrg());
			cronJobResult=CronJobResult.UNKNOWN;
		}
		 return new PerformResult(cronJobResult, CronJobStatus.FINISHED);
	}
	
	private String getMax191LengthString(String value) {
		return value!=null && value.length()>= TEXT_MAX_INDEX?value.substring(TEXT_START_INDEX, TEXT_MAX_INDEX):value;
	}
	
	private String getMax40LengthTextString(String value) {
		return value!=null && value.length()>= MANU_MAX_INDEX?value.substring(TEXT_START_INDEX, MANU_MAX_INDEX):value;
	}
	
	
	private void updateSendToReevooStatus(List <ProductModel> eligibleProducts,CMSSiteModel site) {
		 for(ProductModel reevooProduct:eligibleProducts) {
			 final DistProductSendToReevooModel sendToReevooModel = modelService.create(DistProductSendToReevooModel.class);
			 sendToReevooModel.setProduct(reevooProduct);
			 sendToReevooModel.setSite(site);
			 modelService.save(sendToReevooModel);
		 }
	}
	
	 /**
     * @param category
     * @param productCategoryInfo
     * @return String
     */
    private void fillCategories(final CategoryModel category, final List<String> productCategoryInfo) {
        for (final CategoryModel superCategory : category.getSupercategories()) {
            if ((superCategory != null && !(superCategory instanceof ClassificationClassModel)) && //
                    superCategory.getLevel().equals(Integer.valueOf(category.getLevel().intValue() - 1)) && //
                    superCategory.getLevel().intValue() > 0) {
                productCategoryInfo.add(superCategory.getName());
                fillCategories(superCategory, productCategoryInfo);
            }
        }
    }
	
	private String getImageUrl(ProductModel reevooProduct,String url) {
		if(reevooProduct.getPrimaryImage()!=null && reevooProduct.getPrimaryImage().getMedias()!=null && reevooProduct.getPrimaryImage().getMedias().size() >=1) {
			return getEncodedUrl(url+reevooProduct.getPrimaryImage().getMedias().iterator().next().getURL());
		}else {
			CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(DistConstants.Catalog.DEFAULT_CATALOG_ID, DistConstants.CatalogVersion.ONLINE);
			MediaModel media=mediaService.getMedia(catalogVersion, getConfigurationService().getConfiguration().getString("reevoo.product.default.media"));
			return media.getURL()!=null ? getEncodedUrl(url+media.getURL()) : DistConstants.Punctuation.EMPTY;
		}
	}
	
	private String getEncodedUrl(String imageUrl) {
		String imageFirstPart=imageUrl.substring(TEXT_START_INDEX,imageUrl.lastIndexOf(DistConstants.Punctuation.FORWARD_SLASH)+1);
		String imageLastPart=imageUrl.substring(imageUrl.lastIndexOf(DistConstants.Punctuation.FORWARD_SLASH)+1, imageUrl.length());
		StringBuilder sb = new StringBuilder();
		try {
            sb.append(imageFirstPart+URLEncoder.encode(
                    imageLastPart,
                    java.nio.charset.StandardCharsets.UTF_8.toString()
                  ));
        } catch (UnsupportedEncodingException e) {
           LOG.error("Couldn't encode Product Image URL"+imageUrl);
        }
		return sb.toString();
	}


	public RevooProductFeedExportDao getRevooProductFeedExportDao() {
		return revooProductFeedExportDao;
	}


	public void setRevooProductFeedExportDao(RevooProductFeedExportDao revooProductFeedExportDao) {
		this.revooProductFeedExportDao = revooProductFeedExportDao;
	}



	public DistRevooCsvTransformationService getDistRevooCsvTransformationService() {
		return distRevooCsvTransformationService;
	}




	public void setDistRevooCsvTransformationService(DistRevooCsvTransformationService distRevooCsvTransformationService) {
		this.distRevooCsvTransformationService = distRevooCsvTransformationService;
	}




	public DistReevooExportHelper getReevooExportHelper() {
		return reevooExportHelper;
	}




	public void setReevooExportHelper(DistReevooExportHelper reevooExportHelper) {
		this.reevooExportHelper = reevooExportHelper;
	}
	
	
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
	
	protected CatalogVersionService getCatalogVersionService() {
		return catalogVersionService;
	}

	public void setCatalogVersionService(CatalogVersionService catalogVersionService) {
		this.catalogVersionService = catalogVersionService;
	}
}
