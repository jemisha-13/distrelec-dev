package com.namics.distrelec.b2b.core.obsolescence.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.distrelec.webservice.if15.v1.ReadOrderRequestV2;
import com.distrelec.webservice.if15.v1.ReadOrderResponseV2;
import com.namics.distrelec.b2b.core.event.DistObsolescenceEvent;
import com.namics.distrelec.b2b.core.inout.erp.OrderService;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.ObsolescenceCategoryModel;
import com.namics.distrelec.b2b.core.obsolescence.dao.DistObsolescenceDao;
import com.namics.distrelec.b2b.core.obsolescence.service.DistObsolescenceService;
import com.namics.distrelec.b2b.facades.user.data.ObsolescenceTempData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Resource;
import java.util.*;
import java.util.Map.Entry;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public class DefaultDistObsolescenceService implements DistObsolescenceService{

    private static final Logger LOG = LogManager.getLogger(DefaultDistObsolescenceService.class);

	public static final String SUCCESS = "SUCCESS";

	private static final String SHIPPED_STATUS = "04";
	
	private static final String GERMANY_SALES_ORG_CODE = "7350";

	@Autowired
	private DistObsolescenceDao distObsolescenceDao;
	@Autowired
	private OrderService sapOrderService;
	private UserService userService;
	private EventService eventService;
	
	@Resource
	private CategoryService categoryService;
	
    @Autowired
    private ModelService modelService;
    
    @Autowired
    private ConfigurationService configurationService;
	

	@Override
	public Map<String, List<AbstractOrderEntryModel>> getPhasedOutOrderEntries() {
		
		List<AbstractOrderEntryModel> orderEntries = distObsolescenceDao.findPhasedOutEntries();
		Map<AbstractOrderEntryModel,String> phasedOutOrderEntries = new HashMap<AbstractOrderEntryModel,String>();

			for(AbstractOrderEntryModel orderEntry: orderEntries ) {
				final String erpOrderId=orderEntry.getOrder().getErpOrderCode();
				try {
				final CustomerModel customer = (CustomerModel) orderEntry.getOrder().getUser();
				if(customer!=null && ((B2BCustomerModel) customer).getDefaultB2BUnit()!=null) {
				final String customerSalesOrg=((B2BCustomerModel) customer).getDefaultB2BUnit().getSalesOrg().getCode();
				if( !(GERMANY_SALES_ORG_CODE.equals(customerSalesOrg)) && isOrderEntryPhasedOut(orderEntry,customerSalesOrg) &&(customer.getOptedForObsolescence()!=null && customer.getOptedForObsolescence())){
					
					for(ObsolescenceCategoryModel obsolCategory : customer.getObsolescenceCategories() ) {
						if (orderEntry.getProduct().getPrimarySuperCategory() != null) {
							for (CategoryModel orderCategory : orderEntry.getProduct().getPrimarySuperCategory().getAllSupercategories()) {
								if (obsolCategory.getObsolCategorySelected() && obsolCategory.getCategory().getCode().equals(orderCategory.getCode())) {
									ReadOrderResponseV2 readOrderResponse = getSapOrderDetails(customerSalesOrg, (B2BCustomerModel) customer, erpOrderId);
									if (readOrderResponse != null && readOrderResponse.getOrderStatus().equals(SHIPPED_STATUS)) {
										phasedOutOrderEntries.put(orderEntry, customer.getUid());
									}
								}
							}
						}
					}
				   }
				}
			}catch(Exception ex) {
				LOG.error("Unable to send obsolescence notification mail for the order: "+erpOrderId);
			}	
			}
		
		
		Map<String, List<AbstractOrderEntryModel>> reverseMap = new HashMap<>();

		for (Entry<AbstractOrderEntryModel, String> entry : phasedOutOrderEntries.entrySet()) {
		    if (!reverseMap.containsKey(entry.getValue())) {
		        reverseMap.put(entry.getValue(), new ArrayList<>());
		    }
		    List<AbstractOrderEntryModel> keys = reverseMap.get(entry.getValue());
		    keys.add(entry.getKey());
		    reverseMap.put(entry.getValue(), keys);
		}

		return reverseMap;
	}
	
	private boolean isOrderEntryPhasedOut(AbstractOrderEntryModel orderEntry,String salesOrg) {
		boolean isPhasedOut=false;
		Configuration configuration = getConfigurationService().getConfiguration();
		final String ignoredObsolCategoryCodesConfig = configuration.getString("obsolescence.phasedoutlist");
		List<String> codesList=Arrays.asList(ignoredObsolCategoryCodesConfig.split(","));
		for(String code:codesList) {
			Set<DistSalesOrgProductModel> salesOrgSpecificAttributes=orderEntry.getProduct() !=null ?
					orderEntry.getProduct().getSalesOrgSpecificProductAttributes() : Collections.emptySet();
			
			for(DistSalesOrgProductModel salesOrgProduct:salesOrgSpecificAttributes) {
				if(salesOrg.equals(salesOrgProduct.getSalesOrg().getCode()) && code.equals(salesOrgProduct.getSalesStatus().getCode())) {
					isPhasedOut=true;
					return isPhasedOut;
				}
			}
		}
		return isPhasedOut;
	}
	
	/**
	 * Read order details from SAP
	 *
	 * @param salesOrganization
	 * @param currentUser
	 * @param orderId
	 * @return the {@code ReadOrderResponse}
	 */
	private ReadOrderResponseV2 getSapOrderDetails(final String salesOrganization, final B2BCustomerModel currentUser,
			final String orderId) {

		final B2BUnitModel currentUnit = currentUser.getDefaultB2BUnit();
		final ReadOrderRequestV2 readOrderRequest = new ReadOrderRequestV2();
		readOrderRequest.setSalesOrganization(salesOrganization);
		readOrderRequest.setCustomerId(currentUnit.getErpCustomerID());
		readOrderRequest.setOrderId(orderId);
		return sapOrderService.readOrder(readOrderRequest);
	}

	@Override
	public void sendEmail(Map<String, List<AbstractOrderEntryModel>> pahsedOutOrderEntries) {
		
		
		for (Entry<String, List<AbstractOrderEntryModel>> entry : pahsedOutOrderEntries.entrySet()) {
			CustomerModel customer = (CustomerModel) getUserService().getUserForUID(entry.getKey());
			if (customer.isMarketingCookieConsent()) {
				getEventService().publishEvent(new DistObsolescenceEvent(customer, entry.getValue()));
			}

		}
	}
	

	@Override
	public void unsubscribeObsolescence(String customerUid) {
		CustomerModel customer= (CustomerModel) getUserService().getUserForUID(customerUid);
		customer.setOptedForObsolescence(Boolean.FALSE);
		List<ObsolescenceCategoryModel> obsoleCategoriesList = new ArrayList<>();
		for(ObsolescenceCategoryModel obsoleCategory: customer.getObsolescenceCategories() ) {
			obsoleCategory.setObsolCategorySelected(Boolean.FALSE);
			modelService.save(obsoleCategory);					
			obsoleCategoriesList.add(obsoleCategory);
		}
		customer.setObsolescenceCategories(null);
		customer.setObsolescenceCategories(obsoleCategoriesList);
		customer.setAllObsolCatSelected(Boolean.FALSE);
		modelService.save(customer);		
	}
	
	@Override
	public List<ObsolescenceTempData> changeObsolPreference(List<ObsolescenceTempData> obsoleCategories) {
		
		final CustomerModel customer=(CustomerModel) userService.getCurrentUser();

		for(ObsolescenceTempData obsoleCategory : obsoleCategories) {
			final CategoryModel category = categoryService.getCategoryForCode(obsoleCategory.getCode());
			for(ObsolescenceCategoryModel obsolescenceCategoryModel : customer.getObsolescenceCategories()) {
				if(obsolescenceCategoryModel.getCategory().getCode().equals(category.getCode())) {
					obsolescenceCategoryModel.setObsolCategorySelected(obsoleCategory.isObsolCategorySelected());
				}
				modelService.save(obsolescenceCategoryModel);
			}
		}
		
		customer.setAllObsolCatSelected(isNotEmpty(obsoleCategories) && obsoleCategories.get(0).isAllCatSelected());
		modelService.save(customer);		
		return obsoleCategories;
	}

	@Override
	public void saveObsolescenceCategoriesForCustomer(CustomerModel customer, boolean selected) {
		if (CollectionUtils.isNotEmpty(customer.getObsolescenceCategories())) {
			for (ObsolescenceCategoryModel obsoleCategory : customer.getObsolescenceCategories()) {
				obsoleCategory.setObsolCategorySelected(selected);
				modelService.save(obsoleCategory);
			}
		}
		customer.setOptedForObsolescence(selected);
		customer.setAllObsolCatSelected(selected);
		modelService.save(customer);
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	protected EventService getEventService()
	{
		return eventService;
	}
	
	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
	
	public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
