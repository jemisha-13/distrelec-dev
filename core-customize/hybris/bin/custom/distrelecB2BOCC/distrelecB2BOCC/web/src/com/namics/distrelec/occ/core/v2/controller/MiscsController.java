package com.namics.distrelec.occ.core.v2.controller;

import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.CDN_TTL;
import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.LOCAL_TTL;
import static de.hybris.platform.webservicescommons.cache.CacheControlDirective.PUBLIC;
import static org.springframework.http.ResponseEntity.ok;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import com.namics.distrelec.b2b.core.customer.error.feedback.data.DistCustomerErrorFeedbackData;
import com.namics.distrelec.b2b.facades.basesites.DistrelecOCCBaseSiteFacade;
import com.namics.distrelec.b2b.facades.basesites.seo.DistAllSitesResponse;
import com.namics.distrelec.b2b.facades.basesites.seo.DistLink;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.errorFeedback.DistCustomerFeedbackFacade;
import com.namics.distrelec.b2b.facades.manufacturer.DistManufacturerFacade;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerMenuData;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerMenuResponseData;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.user.data.DistDepartmentDataList;
import com.namics.distrelec.b2b.facades.user.data.DistFunctionDataList;
import com.namics.distrelec.b2b.facades.user.ws.dto.DistDepartmentListWsDTO;
import com.namics.distrelec.b2b.facades.user.ws.dto.DistFunctionsListWsDTO;
import com.namics.distrelec.b2b.facades.vat.eu.DistVatEUFacade;
import com.namics.distrelec.occ.basesites.seo.ws.dto.BaseSiteLinksWsDTO;
import com.namics.distrelec.occ.core.basesite.data.DistManufacturerMenuResponseWSDTO;
import com.namics.distrelec.occ.core.customererrorfeedback.ws.dto.CustomerErrorFeedbackWsDTO;
import com.namics.distrelec.occ.core.order.data.CardTypeDataList;
import com.namics.distrelec.occ.core.readonly.ReadOnly;
import com.namics.distrelec.occ.core.search.data.FactFinderSessionWsDTO;
import com.namics.distrelec.occ.core.security.SecuredAccessConstants;
import com.namics.distrelec.occ.core.storesession.data.CurrencyDataList;
import com.namics.distrelec.occ.core.storesession.data.LanguageDataList;
import com.namics.distrelec.occ.core.user.data.CountryDataList;
import com.namics.distrelec.occ.core.user.data.TitleDataList;
import com.namics.distrelec.occ.core.v2.helper.UsersHelper;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.cmsfacades.rendering.attributeconverters.NavigationNodeToDataContentConverter;
import de.hybris.platform.cmsoccaddon.data.NavigationNodeWsDTO;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.commercewebservicescommons.dto.order.CardTypeListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.storesession.CurrencyListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.storesession.LanguageListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.CountryListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.TitleListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.VatValidationResponseWsDTO;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
@Tag(name = "Miscs")
public class MiscsController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(MiscsController.class);

    @Resource(name = "userFacade")
    private UserFacade userFacade;

    @Resource(name = "storeSessionFacade")
    private StoreSessionFacade storeSessionFacade;

    @Resource(name = "checkoutFacade")
    private CheckoutFacade checkoutFacade;

    @Resource(name = "distCustomerFacade")
    private DistCustomerFacade distCustomerFacade;

    @Resource(name = "distrelecOCCBaseSiteFacade")
    private DistrelecOCCBaseSiteFacade distrelecOCCBaseSiteFacade;

    @Autowired
    private DistManufacturerFacade distManufacturerFacade;

    @Autowired
    private DistVatEUFacade distVatEUFacade;

    @Autowired
    private DistCustomerFeedbackFacade feedbackFacade;

    @Resource
    private UsersHelper usersHelper;

    @Autowired
    private DistCheckoutFacade distCheckoutFacade;

    @Resource(name = "ffSessionIdGenerator")
    private KeyGenerator ffSessionIdGenerator;

    @Resource(name = "customerErrorFeedbackDTOValidator")
    private Validator customerErrorFeedbackDTOValidator;

    @Autowired
    private CMSNavigationService cmsNavigationService;

    @Resource(name = "cmsRenderingNavigationNodeToDataContentConverter")
    private NavigationNodeToDataContentConverter navigationNodeToDataContentConverter;

    @ReadOnly
    @SecurePortalUnauthenticatedAccess
    @RequestMapping(value = "/{baseSiteId}/languages", method = RequestMethod.GET)
    @Cacheable(value = "miscsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getLanguages',#fields)")
    @ResponseBody
    @Operation(operationId = "getLanguages", summary = "Get a list of available languages.", description = "Lists all available languages (all languages used for a particular store). If the list "
                                                                                                           + "of languages for a base store is empty, a list of all languages available in the system will be returned.")
    @ApiBaseSiteIdParam
    public LanguageListWsDTO getLanguages(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final LanguageDataList dataList = new LanguageDataList();
        dataList.setLanguages(storeSessionFacade.getAllLanguages());
        return getDataMapper().map(dataList, LanguageListWsDTO.class, fields);
    }

    @ReadOnly
    @SecurePortalUnauthenticatedAccess
    @RequestMapping(value = "/{baseSiteId}/currencies", method = RequestMethod.GET)
    @Cacheable(value = "miscsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getCurrencies',#fields)")
    @ResponseBody
    @Operation(operationId = "getCurrencies", summary = "Get a list of available currencies.", description = "Lists all available currencies (all usable currencies for the current store). If the list "
                                                                                                             + "of currencies for a base store is empty, a list of all currencies available in the system is returned.")
    @ApiBaseSiteIdParam
    public CurrencyListWsDTO getCurrencies(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final CurrencyDataList dataList = new CurrencyDataList();
        dataList.setCurrencies(storeSessionFacade.getAllCurrencies());
        return getDataMapper().map(dataList, CurrencyListWsDTO.class, fields);
    }

    @RequestMapping(value = "/{baseSiteId}/ffsession", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getFactFinderSession", summary = "Get a fact finder session id (sid)")
    @ApiBaseSiteIdParam
    public FactFinderSessionWsDTO getFactFinderSession() {
        FactFinderSessionWsDTO sessionWsDTO = new FactFinderSessionWsDTO();
        String sessionId = (String) ffSessionIdGenerator.generate();
        sessionWsDTO.setSessionId(sessionId);
        return sessionWsDTO;
    }

    /**
     * @deprecated since 1808. Please use {@link CountriesController#getCountries(String, String)} instead.
     */
    @Deprecated(since = "1808", forRemoval = true)
    @ReadOnly
    @RequestMapping(value = "/{baseSiteId}/deliverycountries", method = RequestMethod.GET)
    @Cacheable(value = "miscsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getDeliveryCountries',#fields)")
    @ResponseBody
    @Operation(operationId = "getDeliveryCountries", summary = "Get a list of shipping countries.", description = "Lists all supported delivery countries for the current store. The list is sorted alphabetically.")
    @ApiBaseSiteIdParam
    public CountryListWsDTO getDeliveryCountries(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final CountryDataList dataList = new CountryDataList();
        dataList.setCountries(checkoutFacade.getDeliveryCountries());
        return getDataMapper().map(dataList, CountryListWsDTO.class, fields);
    }

    @ReadOnly
    @SecurePortalUnauthenticatedAccess
    @RequestMapping(value = "/{baseSiteId}/titles", method = RequestMethod.GET)
    @Cacheable(value = "miscsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getTitles',#fields)")
    @ResponseBody
    @Operation(operationId = "getTitles", summary = "Get a list of all localized titles.", description = "Lists all localized titles.")
    @ApiBaseSiteIdParam
    public TitleListWsDTO getTitles(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final TitleDataList dataList = new TitleDataList();
        dataList.setTitles(userFacade.getTitles());
        return getDataMapper().map(dataList, TitleListWsDTO.class, fields);
    }

    @ReadOnly
    @SecurePortalUnauthenticatedAccess
    @RequestMapping(value = "/{baseSiteId}/cardtypes", method = RequestMethod.GET)
    @Cacheable(value = "miscsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getCardTypes',#fields)")
    @ResponseBody
    @Operation(operationId = "getCardTypes", summary = "Get a list of supported payment card types.", description = "Lists supported payment card types.")
    @ApiBaseSiteIdParam
    public CardTypeListWsDTO getCardTypes(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final CardTypeDataList dataList = new CardTypeDataList();
        dataList.setCardTypes(checkoutFacade.getSupportedCardTypes());
        return getDataMapper().map(dataList, CardTypeListWsDTO.class, fields);
    }

    @ReadOnly
    @RequestMapping(value = "/{baseSiteId}/manufacturerMenu", method = RequestMethod.GET)
    @Cacheable(value = "miscsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true,false,'getManufacturerMenu',#fields)")
    @ResponseBody
    @ApiBaseSiteIdParam
    @Operation(operationId = "manufacturerMenu", summary = "Get manufacturer menu items ", description = "Get all site settings from the store session")
    public DistManufacturerMenuResponseWSDTO getManufacturerMenu(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        List<DistManufacturerMenuData> menuDataList = distManufacturerFacade.getManufacturesForOCC();
        DistManufacturerMenuResponseData distManufacturerMenuResponseData = new DistManufacturerMenuResponseData();
        distManufacturerMenuResponseData.setResponse(menuDataList);
        return getDataMapper().map(distManufacturerMenuResponseData, DistManufacturerMenuResponseWSDTO.class, fields);
    }

    @ReadOnly
    @RequestMapping(value = "/{baseSiteId}/allsiteLinks", method = RequestMethod.GET)
    @Cacheable(value = "miscsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getOCCBaseSiteLinks',#fields)")
    @ResponseBody
    @ApiBaseSiteIdParam
    @Operation(operationId = "getOCCBaseSiteLinks", summary = "Get all base site links for OCC", description = "Get all base sites with corresponding base stores details in FULL mode.")
    public BaseSiteLinksWsDTO getOCCBaseSiteLinks(@PathVariable String baseSiteId,
                                                  @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final List<DistLink> headerLinkLang = distrelecOCCBaseSiteFacade.setupBaseSiteLinks();
        DistAllSitesResponse response = new DistAllSitesResponse();
        response.setFooterLinksLangTags(headerLinkLang);
        return getDataMapper().map(response, BaseSiteLinksWsDTO.class, fields);
    }

    @ReadOnly
    @RequestMapping(value = "/{baseSiteId}/{siteLang}/allsiteLinks", method = RequestMethod.GET)
    @ResponseBody
    @ApiBaseSiteIdParam
    @Operation(operationId = "getOCCBaseSiteLinksForLang", summary = "Get all base site links for OCC for language", description = "Get all base sites with corresponding base stores details in FULL mode.")
    public BaseSiteLinksWsDTO getOCCBaseSiteLinksForLang(@PathVariable String baseSiteId,
                                                         @Parameter(required = true, description = "language") @PathVariable String siteLang,
                                                         @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final List<DistLink> headerLinkLang = distrelecOCCBaseSiteFacade.setupBaseSiteLinksForLang(siteLang);
        DistAllSitesResponse response = new DistAllSitesResponse();
        response.setFooterLinksLangTags(headerLinkLang);
        return getDataMapper().map(response, BaseSiteLinksWsDTO.class, fields);
    }

    @RequestMapping(value = "/{baseSiteId}/isCustomerPresent", method = RequestMethod.POST)
    @Operation(summary = "checks if customer is present")
    @ApiBaseSiteIdParam
    @ResponseBody
    public Boolean isCustomerPresent(@Parameter(description = "id of customer", required = true) @RequestParam final String customerId,
                                     @Parameter(description = "name of customer") @RequestParam(required = false) final String customerName) {
        return distCustomerFacade.searchCustomer(customerId, customerName);
    }

    @RequestMapping(value = "/{baseSiteId}/validateUid", method = RequestMethod.POST)
    @Operation(operationId = "validateUid", summary = "validation of mail-address (uid)", description = "Returns Response of OK(200) or CONFLICT(409).")
    @ApiBaseSiteIdParam
    @ResponseBody
    public ResponseEntity<?> validateUid(@Parameter(required = true) @RequestParam final String uid) {
        boolean doesCustomerExist = distCustomerFacade.doesCustomerExistForUid(uid);
        return doesCustomerExist ? ResponseEntity.status(HttpStatus.CONFLICT).build() : ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/{baseSiteId}/validateVat", method = RequestMethod.POST)
    @Operation(operationId = "validateVAT", summary = "validation of VAT number", description = "Returns the response of form in vat id  field")
    @ApiBaseSiteIdParam
    @ResponseBody
    public VatValidationResponseWsDTO validateVat(@Parameter(required = true, description = "vatNumber") @RequestParam final String vatNumber,
                                                  @Parameter(required = true, description = "countryCode") @RequestParam final String countryCode) {

        boolean validationResult = distVatEUFacade.validateVatNumber(vatNumber, countryCode);
        VatValidationResponseWsDTO vatValidationResponseWsDTO = new VatValidationResponseWsDTO();
        if (validationResult) {
            vatValidationResponseWsDTO.setSuccess(true);
        } else {
            vatValidationResponseWsDTO.setSuccess(false);
        }
        return vatValidationResponseWsDTO;
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_ANONYMOUS, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @PostMapping(value = "/{baseSiteId}/errorfeedback")
    @Operation(operationId = "errorfeedback", summary = "Customer Feedback", description = "Customer Error Feedback Form")
    @ApiBaseSiteIdParam
    @ResponseBody
    public ResponseEntity<?> errorFeedback(
                                           @Parameter(description = "Customer error feedback data", required = true) @RequestBody final CustomerErrorFeedbackWsDTO errorFeedbackWsDTO) {
        validate(errorFeedbackWsDTO, "errorFeedback", customerErrorFeedbackDTOValidator);
        final DistCustomerErrorFeedbackData distCustomerErrorFeedbackData = getDataMapper().map(errorFeedbackWsDTO, DistCustomerErrorFeedbackData.class);

        if (!getFeedbackFacade().saveFeedbackReport(distCustomerErrorFeedbackData)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        getFeedbackFacade().disseminateFeedbackRepor(distCustomerErrorFeedbackData);
        return ok().build();
    }

    @RequestMapping(value = "/{baseSiteId}/departments", method = RequestMethod.GET)
    @Cacheable(value = "miscsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'fetchUserDepartments',#fields)")
    @Operation(operationId = "departments", summary = "returns list of departments about a customer", description = "returns list of departments about a customer")
    @ApiBaseSiteIdParam
    @ResponseBody
    public DistDepartmentListWsDTO fetchUserDepartments(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final DistDepartmentDataList distDepartmentData = new DistDepartmentDataList();
        distDepartmentData.setDepartments(usersHelper.fetchUserDepartments());
        return getDataMapper().map(distDepartmentData, DistDepartmentListWsDTO.class, fields);

    }

    @RequestMapping(value = "/{baseSiteId}/functions", method = RequestMethod.GET)
    @Cacheable(value = "miscsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'fetchUserFunctions',#fields)")
    @Operation(operationId = "functions", summary = "returns list of functions about a customer", description = "returns list of functions about a customer")
    @ApiBaseSiteIdParam
    @ResponseBody
    public DistFunctionsListWsDTO fetchUserFunctions(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final DistFunctionDataList distFuntionData = new DistFunctionDataList();
        distFuntionData.setFunctions(usersHelper.fetchUserFunctions());
        return getDataMapper().map(distFuntionData, DistFunctionsListWsDTO.class, fields);
    }

    @PostMapping(value = "/{baseSiteId}/checkout/payment/notify")
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "checkoutPaymentNotify", summary = "This method notify the checkout payment transaction status.", description = "Gets the data Len and Data from request inputStream")
    @ApiBaseSiteIdParam
    public void checkoutPaymentNotify(@Parameter(description = "currency", required = true) @RequestParam final String currency,
                                      final HttpServletRequest request) throws IOException {
        final StringBuilder notifyRequestBody = new StringBuilder();
        final String requestBody = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        if (StringUtils.isNotBlank(requestBody)) {
            notifyRequestBody.append(requestBody);
            LOG.debug("Notify with data from request body: {}", requestBody);
        } else {
            notifyRequestBody.append("Len=").append(request.getParameter("Len")).append("&Data=").append(request.getParameter("Data"));
            LOG.debug("Notify with data from request parameters: {}", notifyRequestBody);
        }
        distCheckoutFacade.handlePaymentNotify(notifyRequestBody.toString(), currency);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{baseSiteId}/navigation-nodes")
    @ApiBaseSiteIdParam
    @ResponseBody
    @CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
    public ResponseEntity<List<NavigationNodeWsDTO>> getNavigationNodes(
                                                                        @Parameter(required = true, description = "Comma separated list of navigation node ids") @RequestParam String ids,
                                                                        @Parameter(hidden = true) WebRequest webRequest) {
        List<String> idList = Arrays.stream(ids.split(","))
                                    .distinct()
                                    .collect(Collectors.toList());

        List<CMSNavigationNodeModel> navigationNodeModels = idList.stream()
                                                                  .map(id -> {
                                                                      try {
                                                                          return cmsNavigationService.getNavigationNodeForId(id);
                                                                      } catch (CMSItemNotFoundException e) {
                                                                          LOG.warn("Navigation node with id {} not found", id);
                                                                          return null;
                                                                      }
                                                                  })
                                                                  .filter(Objects::nonNull)
                                                                  .collect(Collectors.toList());

        Date lastModified = navigationNodeModels.stream()
                                                .map(this::getNavigationNodeDeepLastModified)
                                                .max(Date::compareTo)
                                                .orElseGet(() -> {
                                                    LOG.warn("Failed to calculate last modified time for navigation nodes {}, using current timestamp instead.",
                                                             ids);
                                                    return new Date();
                                                });

        if (webRequest.checkNotModified(lastModified.getTime())) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                                 .build();
        } else {
            List<NavigationNodeWsDTO> navigationNodeWsDTOS = navigationNodeModels.stream()
                                                                                 .map(navigationNodeToDataContentConverter::convert)
                                                                                 .map(navigationNodeData -> getDataMapper().map(navigationNodeData,
                                                                                                                                NavigationNodeWsDTO.class))
                                                                                 .collect(Collectors.toList());
            return ResponseEntity.ok()
                                 .lastModified(lastModified.getTime())
                                 .body(navigationNodeWsDTOS);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{baseSiteId}/navigation-node/{uid}")
    @ApiBaseSiteIdParam
    @ResponseBody
    @CacheControl(directive = { PUBLIC, CacheControlDirective.NO_CACHE })
    public ResponseEntity<NavigationNodeWsDTO> getNavigationNode(@PathVariable String uid,
                                                                 @Parameter(hidden = true) WebRequest webRequest) throws CMSItemNotFoundException {
        CMSNavigationNodeModel navigationNodeModel = cmsNavigationService.getNavigationNodeForId(uid);
        Date lastModified = getNavigationNodeDeepLastModified(navigationNodeModel);
        if (webRequest.checkNotModified(lastModified.getTime())) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                                 .build();
        } else {
            NavigationNodeData navigationNodeData = navigationNodeToDataContentConverter.convert(navigationNodeModel);
            NavigationNodeWsDTO dto = getDataMapper().map(navigationNodeData, NavigationNodeWsDTO.class);
            return ResponseEntity.ok()
                                 .lastModified(lastModified.getTime())
                                 .body(dto);
        }
    }

    private Date getNavigationNodeDeepLastModified(CMSNavigationNodeModel navNode) {
        Date lastModified = navNode.getModifiedtime();

        if (CollectionUtils.isNotEmpty(navNode.getEntries())) {
            for (CMSNavigationEntryModel navEntry : navNode.getEntries()) {
                if (navEntry.getModifiedtime().after(lastModified)) {
                    lastModified = navEntry.getModifiedtime();
                }
                if (navEntry.getItem() != null && navEntry.getModifiedtime().after(lastModified)) {
                    lastModified = navEntry.getModifiedtime();
                }
            }
        }

        if (CollectionUtils.isNotEmpty(navNode.getChildren())) {
            for (CMSNavigationNodeModel childNavNode : navNode.getChildren()) {
                Date childLastModified = getNavigationNodeDeepLastModified(childNavNode);
                if (childLastModified.after(lastModified)) {
                    lastModified = childLastModified;
                }
            }
        }

        return lastModified;
    }

    public DistCustomerFeedbackFacade getFeedbackFacade() {
        return feedbackFacade;
    }
}
