package com.namics.distrelec.occ.core.v2.controller;

import javax.annotation.Resource;

import com.namics.distrelec.occ.core.readonly.ReadOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.namics.distrelec.b2b.core.event.DistAddressChangeEvent;
import com.namics.distrelec.b2b.facades.infocenter.DistInfoCenterFacade;
import com.namics.distrelec.occ.cms.data.ContentPageNavigationData;
import com.namics.distrelec.occ.cms.ws.dto.ContentPageNavigationWsDTO;
import com.namics.distrelec.occ.core.v2.forms.AddressChangeForm;
import com.namics.distrelec.occ.core.v2.forms.OfflineAddressForm;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Distrelec ContentPage Controller
 */
@Controller
@Tag(name = "ContentPage")
@RequestMapping(value = "/{baseSiteId}/cms")
public class DistContentPageController extends BaseController {

    @Resource(name = "cmsPageService")
    private CMSPageService cmsPageService;

    @Resource(name = "contentPageModelToDataRenderingConverter")
    private Converter<AbstractPageModel, ContentPageNavigationData> contentPageModelToDataRenderingConverter;

    @Resource(name = "addressChangeFormValidator")
    private Validator addressChangeFormValidator;

    @Resource(name = "offlineAddressFormValidator")
    private Validator offlineAddressFormValidator;

    @Autowired
    private DistInfoCenterFacade distInfoCenterFacade;

    private static final Logger LOG = LoggerFactory.getLogger(DistContentPageController.class);

    @ReadOnly
    @RequestMapping(value = "/pages/{pageId}/navigationNodes", method = RequestMethod.GET)
    @CacheControl(directive = CacheControlDirective.PRIVATE, maxAge = 120)
    @ResponseBody
    @Operation(operationId = "get Content Page Navigation Nodes", summary = "Get Content Page Navigation Node details.", description = "Returns details of a single navigation node of a cms page according to a code")
    @ApiBaseSiteIdParam
    public ContentPageNavigationWsDTO getPage(@Parameter(description = "page identifier", required = true) @PathVariable final String pageId,
                                              @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
                                              @PathVariable final String baseSiteId) throws CMSItemNotFoundException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getPage: code=" + sanitize(pageId));
        }
        final AbstractPageModel pageModel = getCmsPageService().getPageForId(pageId);
        return getDataMapper().map(getContentPageModelToDataRenderingConverter().convert(pageModel), ContentPageNavigationWsDTO.class, fields);
    }

    @RequestMapping(value = "/address-change", method = RequestMethod.POST)
    @Operation(operationId = "changeAddress", summary = "Offline address change", description = "Offline address change")
    @ApiBaseSiteIdParam
    @ResponseStatus(HttpStatus.OK)
    public void changeAddress(@Parameter(description = "Address change object", required = true) @RequestBody final AddressChangeForm addressChangeForm) {
        validate(addressChangeForm, "addressChangeForm", addressChangeFormValidator);
        validate(addressChangeForm.getOldAddress(), "oldAddressForm", offlineAddressFormValidator);
        validate(addressChangeForm.getNewAddress(), "newAddressForm", offlineAddressFormValidator);
        getDistInfoCenterFacade().changeAddress(createEvent(addressChangeForm));
    }

    private DistAddressChangeEvent createEvent(final AddressChangeForm addressChangeForm) {
        final DistAddressChangeEvent event = new DistAddressChangeEvent(addressChangeForm.getCustomerNumber(), addressChangeForm.getComment());
        event.setEmailDisplayName(getMessageSource().getMessage("support.displayName", null, "AddressChange", getI18nService().getCurrentLocale()));
        final DistAddressChangeEvent.Address oldAddress = event.new Address();
        final DistAddressChangeEvent.Address newAddress = event.new Address();

        final OfflineAddressForm oldAddressForm = addressChangeForm.getOldAddress();
        if (oldAddressForm != null) {
            BeanUtils.copyProperties(oldAddressForm, oldAddress);
        }
        event.setOldAddress(oldAddress);

        final OfflineAddressForm newAddressForm = addressChangeForm.getNewAddress();
        if (newAddressForm != null) {
            BeanUtils.copyProperties(newAddressForm, newAddress);
        }
        event.setNewAddress(newAddress);

        return event;
    }

    public CMSPageService getCmsPageService() {
        return cmsPageService;
    }

    public Converter<AbstractPageModel, ContentPageNavigationData> getContentPageModelToDataRenderingConverter() {
        return contentPageModelToDataRenderingConverter;
    }

    public DistInfoCenterFacade getDistInfoCenterFacade() {
        return distInfoCenterFacade;
    }
}
