package com.namics.distrelec.b2b.backoffice.editorsearchfacade;

import com.hybris.backoffice.widgets.advancedsearch.engine.AdvancedSearchQueryData;
import com.hybris.cockpitng.editor.commonreferenceeditor.ReferenceEditorSearchFacade;
import com.hybris.cockpitng.search.data.SearchQueryCondition;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.pageable.Pageable;
import com.hybris.cockpitng.search.data.pageable.PageableList;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CompanyAddressValueProvider implements ReferenceEditorSearchFacade<AddressModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyAddressValueProvider.class);

    private static final String OWNER = "owner";
    private static final String SHIPPING_ADDRESS = "shippingAddress";
    private static final String BILLING_ADDRESS = "billingAddress";
    private static final int PAGE_SIZE = 5;
    private ModelService modelService;

    public CompanyAddressValueProvider() {
    }

    public Pageable<AddressModel> search(SearchQueryData searchQueryData) {
        if (searchQueryData instanceof AdvancedSearchQueryData) {
            Optional<CompanyModel> foundCompany = getCompanyModel(searchQueryData);

            if (foundCompany.isPresent()) {
                Optional<Boolean> shippingAddress = isShippingAddress(searchQueryData);
                Optional<Boolean> billingAddress = isBillingAddress(searchQueryData);

                CompanyModel company = (CompanyModel) foundCompany.get();

                List<AddressModel> addresses = company.getAddresses().stream()
                        .filter(address -> !shippingAddress.isPresent() || address.getShippingAddress().equals(shippingAddress.get()))
                        .filter(address -> !billingAddress.isPresent() || address.getBillingAddress().equals(billingAddress.get()))
                        .collect(Collectors.toList());

                this.modelService.refresh(company);
                return new PageableList(addresses, PAGE_SIZE);
            }
        }

        return new PageableList(new ArrayList(), PAGE_SIZE);
    }

    private Optional<CompanyModel> getCompanyModel(SearchQueryData searchQueryData) {
        return ((AdvancedSearchQueryData) searchQueryData).getConditions(true).stream()
                .filter((condition) -> OWNER.equals(condition.getDescriptor().getAttributeName()))
                .map(SearchQueryCondition::getValue)
                .filter(CompanyModel.class::isInstance)
                .map(CompanyModel.class::cast)
                .findFirst();
    }

    private Optional<Boolean> isShippingAddress(SearchQueryData searchQueryData) {
        return ((AdvancedSearchQueryData) searchQueryData).getConditions(true).stream()
                .filter((condition) -> SHIPPING_ADDRESS.equals(condition.getDescriptor().getAttributeName()))
                .map(SearchQueryCondition::getValue)
                .map(String.class::cast)
                .map(Boolean::parseBoolean)
                .findFirst();
    }

    private Optional<Boolean> isBillingAddress(SearchQueryData searchQueryData) {
        return ((AdvancedSearchQueryData) searchQueryData).getConditions(true).stream()
                .filter((condition) -> BILLING_ADDRESS.equals(condition.getDescriptor().getAttributeName()))
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
