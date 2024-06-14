package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.event.DistSendToFriendEvent;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.misc.DistShareWithFriendsFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.product.data.DistCompareFeatureData;
import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.SendToFriendForm;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.product.data.ClassificationData;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.ProductData;

@Controller
@RequestMapping(value = "/compare")
public class CompareListPageController extends AbstractPageController {

    private static final String COMPARE_LIST_CMS_PAGE = "compare";

    private static final String PRODUCTS_KEY = "compareProducts";

    private static final String META_PRODUCTS_KEY = "metaCompareProducts";

    private static final String PRODUCT_CODE = "productCode";

    private static final int METAHD_LISTSIZE_DEFAULT_VALUE = 5;

    @Autowired
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Autowired
    private DistShareWithFriendsFacade distShareWithFriendsFacade;

    @Autowired
    private DistrelecProductFacade productFacade;

    @GetMapping
    public String comparePage(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        final String compareList = Attributes.COMPARE.getValueFromCookies(request);
        final AbstractPageModel comparePage = getCompareListPage();
        comparePage.setWtAreaCode("cp");
        storeCmsPageInModel(model, comparePage);
        final List<ProductData> products = getDistCompareListFacade().getCompareList(compareList);

        List<ProductData> punchedOutProducts = products
                                                       .stream()
                                                       .filter(product -> productFacade.isProductPunchedOut(product.getCode()))
                                                       .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(punchedOutProducts)) {
            punchedOutProducts.forEach(product -> getDistCompareListFacade().removeFromCompareList(normalizeProductCode(product.getCode()), compareList));
            products.removeAll(punchedOutProducts);
            GlobalMessages.addErrorMessage(model, "compare.product.error.punchout", new String[] { getJoinedPunchedOutProductCodes(punchedOutProducts) },
                                           DistConstants.Punctuation.PIPE);
        }
        populateAttributes(products);
        model.addAttribute(PRODUCTS_KEY, products);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, simpleBreadcrumbBuilder.getBreadcrumbs(comparePage.getTitle(), comparePage.getTitle(Locale.ENGLISH)));

        return getViewForPage(model);
    }

    private String getJoinedPunchedOutProductCodes(List<ProductData> punchedOutProducts) {
        return punchedOutProducts
                                 .stream()
                                 .map(ProductData::getCode)
                                 .collect(Collectors.joining(", "));
    }

    @GetMapping(value = "/popup")
    public String getCompareList(final Model model, final HttpServletRequest request) {
        final String compareList = Attributes.COMPARE.getValueFromCookies(request);
        final int maxListSize = getConfigurationService().getConfiguration().getInt(DistConstants.PropKey.Shop.COMPARE_METAHD_LIST_SIZE,
                                                                                    METAHD_LISTSIZE_DEFAULT_VALUE);
        model.addAttribute(PRODUCTS_KEY, getDistCompareListFacade().getCompareList(compareList, maxListSize));
        return ControllerConstants.Views.Fragments.Wishlist.ComparePopup;
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public String addToCompareBulk(@RequestParam(value = "productCodes[]") final String[] products, final Model model, final HttpServletRequest request,
                                   final HttpServletResponse response) {
        String compareList = Attributes.COMPARE.getValueFromCookies(request);
        for (final String productCode : products) {
            compareList = getDistCompareListFacade().addToCompareList(normalizeProductCode(productCode), compareList);
        }
        if (compareList != null) {
            Attributes.COMPARE.setValue(request, response, compareList);
        } else {
            Attributes.COMPARE.removeValue(response);
        }
        final int maxListSize = getConfigurationService().getConfiguration().getInt(DistConstants.PropKey.Shop.COMPARE_METAHD_LIST_SIZE,
                                                                                    METAHD_LISTSIZE_DEFAULT_VALUE);
        model.addAttribute(META_PRODUCTS_KEY, getDistCompareListFacade().getCompareList(compareList, maxListSize));
        return ControllerConstants.Views.Fragments.Wishlist.CompareProductsJson;
    }

    @PostMapping(value = "/remove", produces = MediaType.APPLICATION_JSON_VALUE)
    public String removeFromShoppingList(@RequestParam(value = PRODUCT_CODE) final String productCode, final Model model, final HttpServletRequest request,
                                         final HttpServletResponse response) {
        final String compareList = Attributes.COMPARE.getValueFromCookies(request);
        final String newCompareList = getDistCompareListFacade().removeFromCompareList(normalizeProductCode(productCode), compareList);
        if (newCompareList != null) {
            Attributes.COMPARE.setValue(request, response, newCompareList);
        } else {
            Attributes.COMPARE.removeValue(response);
        }
        final int maxListSize = getConfigurationService().getConfiguration().getInt(DistConstants.PropKey.Shop.COMPARE_METAHD_LIST_SIZE,
                                                                                    METAHD_LISTSIZE_DEFAULT_VALUE);
        model.addAttribute(META_PRODUCTS_KEY, getDistCompareListFacade().getCompareList(newCompareList, maxListSize));
        return ControllerConstants.Views.Fragments.Wishlist.CompareProductsJson;
    }

    @PostMapping(value = "/removeAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public String removeAllFromShoppingList(final Model model, final HttpServletRequest request, final HttpServletResponse response) {
        // Remove the compare list values from the Cookie
        Attributes.COMPARE.removeValue(response);
        final String compareList = Attributes.COMPARE.getValueFromCookies(request);
        getDistCompareListFacade().removeAllFromCompareList(compareList);
        model.addAttribute(META_PRODUCTS_KEY, Collections.EMPTY_LIST);
        return ControllerConstants.Views.Fragments.Wishlist.CompareProductsJson;
    }

    @GetMapping(value = "/metaCompareProducts", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getMetaCompareProducts(final Model model, final HttpServletRequest request) {
        final String compareList = Attributes.COMPARE.getValueFromCookies(request);
        final int maxListSize = getConfigurationService().getConfiguration().getInt(DistConstants.PropKey.Shop.COMPARE_METAHD_LIST_SIZE,
                                                                                    METAHD_LISTSIZE_DEFAULT_VALUE);
        model.addAttribute(META_PRODUCTS_KEY, getDistCompareListFacade().getCompareList(compareList, maxListSize));
        return ControllerConstants.Views.Fragments.Wishlist.CompareProductsJson;
    }

    @PostMapping(value = "/sendToFriend")
    @ResponseBody
    public Map<String, String> sendCompareListToFriendInJSON(@Valid final SendToFriendForm form, final BindingResult bindingResults,
                                                             final HttpServletRequest request) {
        final Map<String, String> result = new HashMap<>();
        if (!getCaptchaUtil().validateReCaptcha(request)) {
            result.put("errorCode", "captcha");
            return result;
        }

        if (bindingResults.hasErrors()) {
            result.put("errorCode", "unknown");
            return result;
        }

        final DistSendToFriendEvent sendToFriendEvent = getSendToFriendEvent(form);
        final String compareList = Attributes.COMPARE.getValueFromCookies(request);
        final List<ProductData> products = getDistCompareListFacade().getCompareList(compareList);
        distShareWithFriendsFacade.shareProductComparisonWithFriends(sendToFriendEvent, products);

        result.put("errorCode", StringUtils.EMPTY);
        return result;
    }

    protected AbstractPageModel getCompareListPage() throws CMSItemNotFoundException {
        return getContentPageForLabelOrId(COMPARE_LIST_CMS_PAGE);
    }

    protected void populateAttributes(final List<ProductData> products) {
        setAttributesInProducts(products, evaluateCommonFeatureCodes(products));
    }

    protected List<DistCompareFeatureData> evaluateCommonFeatureCodes(final List<ProductData> products) {
        final Map<String, DistCompareFeatureData> allFeatures = new HashMap<>();
        for (final ProductData product : products) {
            if (product.getEndOfLifeDate() == null && product.isBuyable()) {
                final List<DistCompareFeatureData> compareFeatures = getCompareFeatures(product.getClassifications());
                for (final DistCompareFeatureData compareFeature : compareFeatures) {
                    String code = compareFeature.getCode();
                    final int index = code.lastIndexOf(DistConstants.Punctuation.DOT);
                    if (index >= 0) {
                        code = compareFeature.getCode().substring(index + 1);
                    }

                    // Setting the optimized feature code
                    compareFeature.setCode(code);

                    if (allFeatures.containsKey(code)) {
                        allFeatures.get(code).incrementCounter();
                    } else {
                        allFeatures.put(code, compareFeature);
                    }
                }
            }
        }
        final List<DistCompareFeatureData> sortedFeatures = new ArrayList<>(allFeatures.values());
        Collections.sort(sortedFeatures);
        return sortedFeatures;
    }

    protected void setAttributesInProducts(final List<ProductData> products, final List<DistCompareFeatureData> allFeatures) {
        final Map<String, String> possibleCommonAttributes = new HashMap<>();
        final Map<String, String> possibleOtherAttributes = new HashMap<>();

        for (final ProductData product : products) {
            final Map<String, FeatureData> commonAttributes = new HashMap<>();
            final Map<String, FeatureData> otherAttributes = new HashMap<>();

            final Map<String, FeatureData> features = getFeatures(product.getClassifications());

            for (final DistCompareFeatureData feature : allFeatures) {
                final FeatureData tempFeature = features.containsKey(feature.getCode()) ? features.get(feature.getCode()) : createEmptyFeature(feature);

                if (feature.getCounter() > 1 || products.size() == 1) {
                    commonAttributes.put(tempFeature.getCode(), tempFeature);
                    if (!possibleCommonAttributes.containsKey(tempFeature.getCode())) {
                        possibleCommonAttributes.put(tempFeature.getCode(), tempFeature.getName());
                    }
                } else if (tempFeature.getFeatureValues() != null) {
                    otherAttributes.put(tempFeature.getCode(), tempFeature);
                    if (!possibleOtherAttributes.containsKey(tempFeature.getCode())) {
                        possibleOtherAttributes.put(tempFeature.getCode(), tempFeature.getName());
                    }
                }
            }

            product.setCommonAttributes(commonAttributes);
            product.setPossibleCommonAttributes(possibleCommonAttributes);
            product.setOtherAttributes(otherAttributes);
            product.setPossibleOtherAttributes(possibleOtherAttributes);
        }
    }

    protected List<DistCompareFeatureData> getCompareFeatures(final Collection<ClassificationData> classifications) {
        final List<DistCompareFeatureData> compareFeatures = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(classifications)) {
            for (final ClassificationData classification : classifications) {
                if (CollectionUtils.isNotEmpty(classification.getFeatures())) {
                    for (final FeatureData feature : classification.getFeatures()) {
                        final DistCompareFeatureData compareFeature = new DistCompareFeatureData();
                        compareFeature.setCode(feature.getCode());
                        compareFeature.setName(feature.getName());
                        compareFeature.setDescription(feature.getDescription());
                        compareFeature.setVisibility(feature.getVisibility());
                        compareFeature.setPosition(feature.getPosition());
                        compareFeatures.add(compareFeature);
                    }
                }
            }
        }
        return compareFeatures;
    }

    protected Map<String, FeatureData> getFeatures(final Collection<ClassificationData> classifications) {
        final Map<String, FeatureData> featureMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(classifications)) {
            for (final ClassificationData classification : classifications) {
                if (CollectionUtils.isNotEmpty(classification.getFeatures())) {
                    for (final FeatureData feature : classification.getFeatures()) {
                        String code = feature.getCode();
                        final int index = code.lastIndexOf(DistConstants.Punctuation.DOT);
                        if (index >= 0) {
                            code = feature.getCode().substring(index + 1);
                        }
                        feature.setCode(code);
                        featureMap.put(code, feature);
                    }
                }
            }
        }
        return featureMap;
    }

    protected FeatureData createEmptyFeature(final DistCompareFeatureData feature) {
        final FeatureData emptyFeature = new FeatureData();
        emptyFeature.setCode(feature.getCode());
        emptyFeature.setName(feature.getName());
        emptyFeature.setDescription(feature.getDescription());
        emptyFeature.setVisibility(feature.getVisibility());
        emptyFeature.setPosition(feature.getPosition());
        emptyFeature.setFeatureValues(null);
        emptyFeature.setFeatureUnit(null);
        return emptyFeature;
    }

    public SimpleBreadcrumbBuilder getSimpleBreadcrumbBuilder() {
        return simpleBreadcrumbBuilder;
    }
}
