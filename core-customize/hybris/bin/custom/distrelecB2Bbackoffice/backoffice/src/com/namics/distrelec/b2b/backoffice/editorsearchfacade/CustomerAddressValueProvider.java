package com.namics.distrelec.b2b.backoffice.editorsearchfacade;

import com.hybris.backoffice.widgets.advancedsearch.engine.AdvancedSearchQueryData;
import com.hybris.cockpitng.editor.commonreferenceeditor.ReferenceEditorSearchFacade;
import com.hybris.cockpitng.search.data.SearchQueryCondition;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.pageable.Pageable;
import com.hybris.cockpitng.search.data.pageable.PageableList;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomerAddressValueProvider implements ReferenceEditorSearchFacade<AddressModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerAddressValueProvider.class);

    private static final String OWNER = "owner";
    private static final String CONTACT_ADDRESS = "contactAddress";
    private static final int PAGE_SIZE = 5;
    private ModelService modelService;

    public CustomerAddressValueProvider() {
    }

    public Pageable<AddressModel> search(SearchQueryData searchQueryData) {
        if (searchQueryData instanceof AdvancedSearchQueryData) {
            Optional<CustomerModel> foundCustomer = getCustomerModel(searchQueryData);

            if (foundCustomer.isPresent()) {
                Optional<Boolean> customerAddress = isCustomerAddress(searchQueryData);

                CustomerModel customer = (CustomerModel) foundCustomer.get();

                List<AddressModel> addresses = customer.getAddresses().stream()
                        .filter(address -> !customerAddress.isPresent() || address.getContactAddress().equals(customerAddress.get()))
                        .collect(Collectors.toList());

                this.modelService.refresh(customer);
                return new PageableList(addresses, PAGE_SIZE);
            }
        }

        return new PageableList(new ArrayList(), PAGE_SIZE);
    }

    private Optional<CustomerModel> getCustomerModel(SearchQueryData searchQueryData) {
        return ((AdvancedSearchQueryData) searchQueryData).getConditions(true).stream()
                .filter((condition) -> OWNER.equals(condition.getDescriptor().getAttributeName()))
                .map(SearchQueryCondition::getValue)
                .filter(CustomerModel.class::isInstance)
                .map(CustomerModel.class::cast)
                .findFirst();
    }

    private Optional<Boolean> isCustomerAddress(SearchQueryData searchQueryData) {
        return ((AdvancedSearchQueryData) searchQueryData).getConditions(true).stream()
                .filter((condition) -> CONTACT_ADDRESS.equals(condition.getDescriptor().getAttributeName()))
                .map(SearchQueryCondition::getValue)
                .map(String.class::cast)
                .map(Boolean::parseBoolean)
                .findFirst();
    }

    public ModelService getModelService() {
        return this.modelService;
    }

    @Autowired
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

}
