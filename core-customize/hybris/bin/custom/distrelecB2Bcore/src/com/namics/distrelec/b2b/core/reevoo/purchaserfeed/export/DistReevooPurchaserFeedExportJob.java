package com.namics.distrelec.b2b.core.reevoo.purchaserfeed.export;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.jobs.DistReevooPurchaserFeedCronJobModel;
import com.namics.distrelec.b2b.core.reevoo.productfeed.export.DistReevooExportServiceException;
import com.namics.distrelec.b2b.core.reevoo.productfeed.export.DistRevooCsvTransformationService;
import com.namics.distrelec.b2b.core.reevoo.purchaserfeed.smc.DistSMCServiceImpl;
import com.namics.distrelec.b2b.core.service.newsletter.model.DistMarketingConsentProfileModel;
import com.namics.distrelec.b2b.facades.user.data.DistMarketingConsentData;
import com.namics.hybris.toolbox.CollectionUtil;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.i18n.LanguageResolver;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;

public class DistReevooPurchaserFeedExportJob extends AbstractJobPerformable<DistReevooPurchaserFeedCronJobModel> {

	private static final Logger LOG = LogManager.getLogger(DistReevooPurchaserFeedExportJob.class);

	private static final String BLANK_STRING = "";
	
	private static final String LANGUAGE_LABEL_BRACKET = "(";

	private static final String DATE_PATTERN = "dd/MM/yyyy";
	
	private static final String B2E_USER_TYPE= "B2E";

	private static final String CONFIGURATION_SEPERATOR = ",";
	
	private static final String GERMANY_UID="distrelec_DE";


	@Autowired
	DistReevooPurchaserFeedDao revooPurchaserFeedExportDao;
	
	@Autowired
	DistPurchaserFeedReevooHelper distPurchaserFeedReevooHelper;
	
	@Autowired
	private ConfigurationService configurationService;
	
	@Autowired
	private CommonI18NService commonI18NService;
	
	@Autowired
	private UserService userService;
    
    @Autowired
    DistSMCServiceImpl distSMCServiceImpl;
	
	@Override
	public PerformResult perform(DistReevooPurchaserFeedCronJobModel cronJob) {
		CronJobResult cronJobResult = CronJobResult.SUCCESS;
		CMSSiteModel site = cronJob.getSites().iterator().next();
		if (site.isReevooActivated()) {
			try {
				Calendar startDateCalendar = Calendar.getInstance();
				startDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
				startDateCalendar.set(Calendar.MINUTE, 0);
				startDateCalendar.set(Calendar.SECOND, 0);
				startDateCalendar.set(Calendar.MILLISECOND, 0);
				Calendar endDateCalendar = Calendar.getInstance();
				endDateCalendar.set(Calendar.HOUR_OF_DAY, 23);
				endDateCalendar.set(Calendar.MINUTE, 59);
				endDateCalendar.set(Calendar.SECOND, 59);
				endDateCalendar.set(Calendar.MILLISECOND, 0);
				if(!getConfigurationService().getConfiguration().getBoolean("reevoo.purchaserfeed.cron.mode.dev",Boolean.FALSE)) {
					endDateCalendar.add(Calendar.DAY_OF_MONTH, -1);
				}
				
				if (cronJob.getIsHistoricMode() != null && cronJob.getIsHistoricMode()) {
					startDateCalendar.add(Calendar.DAY_OF_MONTH, -getConfigurationService().getConfiguration().getInt("reevoo.purchaserfeed.historic.days", 30));
				}else {
					if(site.getReevooPurchaserDuration()!=null ) {
						startDateCalendar.add(Calendar.DAY_OF_MONTH, -site.getReevooPurchaserDuration());
					}else {
						startDateCalendar.add(Calendar.DAY_OF_MONTH, -getConfigurationService().getConfiguration().getInt("reevoo.purchaserfeed.incremental.days", 30));
					}
				}
				Date startDate = startDateCalendar.getTime();
				Date endDate=endDateCalendar.getTime();
				List<OrderModel> orderList = revooPurchaserFeedExportDao.getEligibleOrderEntry(site.getSalesOrg(), site,
						startDate, endDate);
				LOG.info("Cronjob Trigerred" + orderList.size());
				final Map<LanguageModel,List<String[]>> resultMap =new HashMap <LanguageModel,List<String[]>>();
				Collection<LanguageModel> supportedLanguages=cronJob.getSupportedLanguages();
				for(LanguageModel supportedLanguage:supportedLanguages) {
					resultMap.put(supportedLanguage, new ArrayList<String[]>());
				}
				HashMap<String,String> customerOrderIdMap=new HashMap<String,String>();
				for (OrderModel order : orderList) {
					final B2BCustomerModel currentCustomer = (B2BCustomerModel) order.getUser();
					if(!isCustomerTypeEligibleForExport(currentCustomer)) {
						continue;
					}
					AddressModel addressModel = currentCustomer.getContactAddress();
					AddressModel billingAddress=order.getPaymentAddress();
					Boolean isB2EUser=isB2E(currentCustomer);
					if(addressModel==null && billingAddress!=null) {
						addressModel=billingAddress;
					}
					Map<String,Boolean> userEligibleMap=new HashMap<String,Boolean>(); 
					for (AbstractOrderEntryModel entry : order.getEntries()) {
						if(entry.getProduct().getReevooAvailable()!=null && entry.getProduct().getReevooAvailable() && billingAddress !=null &&isProductSalesStatusValid(entry.getProduct(),site.getSalesOrg()) && (currentCustomer.getEligibleForReevoo() !=null && currentCustomer.getEligibleForReevoo())) {
							if(order.getSite() !=null && "distrelec_DE".equalsIgnoreCase(order.getSite().getUid())) {
								String email=isB2EUser ? billingAddress.getEmail() :addressModel.getEmail();
								if(!userEligibleMap.containsKey(email)) {
									Boolean isOrderEligibleForReevoo=isUserEligibleForReevoo(order.getSite(),email);
									userEligibleMap.put(email, isOrderEligibleForReevoo);
								}
								if(!userEligibleMap.get(email)) {
									LOG.info("Order is not eligible fpr Reevoo"+order.getCode());
									continue;
								}
							}
							String[] csvRow = new String[13];
							csvRow[0] = entry.getProduct().getCode();
							csvRow[1] = isB2EUser ?  billingAddress.getTitle() !=null ? billingAddress.getTitle().getName() : BLANK_STRING :  currentCustomer.getTitle() != null ? currentCustomer.getTitle().getName() : BLANK_STRING;
							csvRow[2] = isB2EUser ? billingAddress.getFirstname():addressModel.getFirstname();
							csvRow[3] = isB2EUser ? billingAddress.getEmail() :addressModel.getEmail();
							csvRow[4] = isB2EUser? billingAddress.getCountry()!=null ? billingAddress.getCountry().getName() : BLANK_STRING : addressModel.getCountry() != null ? addressModel.getCountry().getName()
									: BLANK_STRING;
							csvRow[5] = order.getLanguage()!=null && order.getLanguage().getName()!=null ? getOrderLanguageLabel(order.getLanguage().getName()) :BLANK_STRING;
							csvRow[6] = (new SimpleDateFormat(DATE_PATTERN)).format(order.getCreationtime());
							//For particualr feed we want to send all the orders of customer under single order is so that customer receives just single email
							if(customerOrderIdMap.containsKey(currentCustomer.getPk().toString())) {
								csvRow[7] = customerOrderIdMap.get(currentCustomer.getPk().toString());
							}else {
								csvRow[7] = order.getErpOrderCode();								
							}
							csvRow[8] = currentCustomer.getErpContactID();
							csvRow[9] = entry.getTotalPrice() != null ? entry.getTotalPrice().toString() : BLANK_STRING;
							csvRow[10] = order.getCurrency() != null ? order.getCurrency().getIsocode() : BLANK_STRING;
							csvRow[11] = order.getRequestedDeliveryDate() != null
									? (new SimpleDateFormat(DATE_PATTERN)).format(order.getRequestedDeliveryDate())
									: (new SimpleDateFormat(DATE_PATTERN)).format(order.getCreationtime());
							csvRow[12] = isB2EUser ? B2E_USER_TYPE :  currentCustomer.getCustomerType() != null
									? currentCustomer.getCustomerType().getCode()
									: BLANK_STRING;
							if(!customerOrderIdMap.containsKey(currentCustomer.getPk().toString())) {
								customerOrderIdMap.put(currentCustomer.getPk().toString(), order.getErpOrderCode());
							}
							String orderLanguageCode=order.getLanguage().getIsocode();
							if(orderLanguageCode!=null && !orderLanguageCode.contains("_")  && resultMap.get(order.getLanguage())!=null) {
								(resultMap.get(order.getLanguage())).add(csvRow);
							}else if(orderLanguageCode!=null && orderLanguageCode.contains("_")) {
								List<String> codesList=Arrays.asList(orderLanguageCode.split("_"));
								LanguageModel language=commonI18NService.getLanguage(codesList.get(0));
								if(!org.fest.util.Collections.isEmpty(codesList) && resultMap.get(language)!=null) {
									resultMap.get(language).add(csvRow);
								}else {
									(resultMap.get(order.getLanguage())).add(csvRow);
								}
							}
							csvRow = null;
						}
						}
					}

				
					
				LOG.info("Export Trigerred for" + cronJob.getCode() + ".Size of Export ::" + orderList.size());
				

				try {
					distPurchaserFeedReevooHelper.saveExportData(resultMap, cronJob);
					LOG.info("Export Finished for" + cronJob.getCode());
				} catch (final DistReevooPurchaserExportServiceException e) {
					LOG.error("Reevoo purchaser related exception occurred during export", e);
					return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
				} catch (final DistReevooExportServiceException e) {
					LOG.error("Reevoo related exception occurred during export", e);
					return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
				} catch (final Exception e) {
					LOG.error("Exception occurred during export", e);
					return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
				} 

			
			}catch (Exception ex) {
				LOG.error("Error in exporting Reevoo Product Feed Data for " + cronJob.getCode(), ex);
				cronJobResult = CronJobResult.ERROR;
			}
		} else {
			LOG.warn("Reevoo Product Feed not activated for Sales Org" + site.getSalesOrg());
			cronJobResult = CronJobResult.UNKNOWN;
		}
		return new PerformResult(cronJobResult, CronJobStatus.FINISHED);
	}
	
	private String getOrderLanguageLabel(String language) {
		if(language.contains(LANGUAGE_LABEL_BRACKET)) {
			return language.substring(0, language.indexOf(LANGUAGE_LABEL_BRACKET)).trim();
		}
		return language;
	}
	
	private boolean isProductSalesStatusValid(ProductModel product,DistSalesOrgModel salesOrg) {
		Boolean isValid=false;
		Set<DistSalesOrgProductModel> salesOrgSpecificAttributes=product !=null ? product.getSalesOrgSpecificProductAttributes():Collections.emptySet();
		final String ignoredObsolCategoryCodesConfig = configurationService.getConfiguration().getString("reevoo.product.eligible.status");
		List<String> codesList=Arrays.asList(ignoredObsolCategoryCodesConfig.split(","));
		for(String code:codesList) {
			for(DistSalesOrgProductModel salesOrgProduct:salesOrgSpecificAttributes) {
				if(salesOrg.getCode().equals(salesOrgProduct.getSalesOrg().getCode()) && code.equals(salesOrgProduct.getSalesStatus().getCode())) {
					isValid=true;
					return isValid;
				}
			}
		}
		return isValid;
	}
	
	private Boolean isUserEligibleForReevoo(BaseSiteModel site ,String email) {
		Boolean isEligible=false;
		if(site !=null && GERMANY_UID.equalsIgnoreCase(site.getUid())) {
			isEligible= distSMCServiceImpl.isUserSubscribedToEmail(email) && distSMCServiceImpl.isUserSubscribedToSurvey(email);
		}else {
			isEligible=true;
		}
		return isEligible;
	}
	
	private boolean isCustomerTypeEligibleForExport(B2BCustomerModel currentCustomer) {
		String[] nonEligibleType=getConfigurationService().getConfiguration().getString("reevoo.purchaserfeed.noneligible.customer.type").split(CONFIGURATION_SEPERATOR);
		if(currentCustomer.getDistFunction()!=null && Arrays.asList(nonEligibleType).contains(currentCustomer.getDistFunction().getCode())) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	
	protected ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public DistReevooPurchaserFeedDao getRevooPurchaserFeedExportDao() {
		return revooPurchaserFeedExportDao;
	}



	public void setRevooPurchaserFeedExportDao(DistReevooPurchaserFeedDao revooPurchaserFeedExportDao) {
		this.revooPurchaserFeedExportDao = revooPurchaserFeedExportDao;
	}
	
	public DistPurchaserFeedReevooHelper getReevooExportHelper() {
		return distPurchaserFeedReevooHelper;
	}

	 protected boolean isB2E(final B2BCustomerModel contact) {
	        return getUserService().isMemberOfGroup(contact, getUserService().getUserGroupForUID(DistConstants.User.B2BEESHOPGROUP_UID));
	 }
	 
	 public UserService getUserService() {
	        return userService;
	    }

	    public void setUserService(UserService userService) {
	        this.userService = userService;
	    }


	public void setReevooExportHelper(DistPurchaserFeedReevooHelper reevooExportHelper) {
		this.distPurchaserFeedReevooHelper = reevooExportHelper;
	}
	
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

}

