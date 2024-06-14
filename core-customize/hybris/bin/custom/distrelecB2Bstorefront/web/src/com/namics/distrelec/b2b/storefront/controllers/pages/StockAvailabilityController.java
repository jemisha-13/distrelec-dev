package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import de.hybris.platform.core.model.product.ProductModel;

import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;

import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

@Controller
public class StockAvailabilityController extends AbstractPageController {

	@Autowired
	private ProductService productService;

	@Autowired
	@Qualifier("productFacade")
	private DistrelecProductFacade productFacade;

	public static final String PRODUCT_INFORMATION_MAPPING = "/**/productinfo";

	public static final String PRODUCT_INFORMATION_ALL_COUNTRIES_MAPPING = "/**/pinfoallcountries";

	public static final String PRODUCT_INFORMATION_ALL_COUNTRIES_QUERY_PARAMETER_NAME = "pc";

	public static final String PRODUCT_INFORMATION_QUERY_PARAMETER_NAME = DistrelecfactfindersearchConstants.PRODUCT_INFORMATION_QUERY_PARAMETER_NAME;

	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public DistrelecProductFacade getProductFacade() {
		return productFacade;
	}

	public void setProductFacade(DistrelecProductFacade productFacade) {
		this.productFacade = productFacade;
	}

	@RequestMapping(value = PRODUCT_INFORMATION_MAPPING, method = RequestMethod.GET)
	public String productInformation(@RequestParam(value = PRODUCT_INFORMATION_QUERY_PARAMETER_NAME, required = true)
	final String searchQuery, final Model model, final HttpServletRequest request) {
		if (StringUtils.isNumeric(searchQuery)) {
			final ProductModel productModel = getProductService().getProductForCode(searchQuery);
			if (productModel != null && StringUtils.isNotBlank(productModel.getCode())) {
				final List<ProductAvailabilityData> availabilityData = getProductFacade()
						.getAvailability(Collections.singletonList(productModel.getCode()));
				if (CollectionUtils.isNotEmpty(availabilityData)) {
					final ProductAvailabilityData productAvailabilityData = availabilityData.get(0);
					model.addAttribute("articleNumber", searchQuery);
					model.addAttribute("deliveryTime", productAvailabilityData.getStatusLabel());
					model.addAttribute("stock", productAvailabilityData.getStockLevelTotal());
					addGlobalModelAttributes(model, request);
					return ControllerConstants.Views.Pages.Product.ProductInformationPage;
				}
			}
		}

		return addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/notFound");
	}

	@RequestMapping(value = PRODUCT_INFORMATION_ALL_COUNTRIES_MAPPING, method = RequestMethod.GET)
	public String productInformationAllCountries(
			@RequestParam(value = PRODUCT_INFORMATION_ALL_COUNTRIES_QUERY_PARAMETER_NAME, required = true)
			final String searchQuery, final Model model, final HttpServletRequest request) {
		if (StringUtils.isNumeric(searchQuery)) {
			final ProductModel productModel = getProductService().getProductForCode(searchQuery);
			if (productModel != null && StringUtils.isNotBlank(productModel.getCode())) {
				final List<ProductAvailabilityData> availabilityData = getProductFacade()
						.getAvailabilityAllCountries(Collections.singletonList(productModel.getCode()), Boolean.FALSE);
				if (CollectionUtils.isNotEmpty(availabilityData)) {
					model.addAttribute("articleNumber", searchQuery);
					model.addAttribute("productinfo", availabilityData);
					addGlobalModelAttributes(model, request);
					return ControllerConstants.Views.Pages.Product.ProductInformationAllCountriesPage;
				}
			}
		}
		return addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/notFound");
	}

	@ExceptionHandler(UnknownIdentifierException.class)
	public String handleUnknownIdentifierException(final SystemException exception, final HttpServletRequest request) {
		final String uuidString = java.util.UUID.randomUUID().toString();
		if (ERROR_PAGE_LOG.isDebugEnabled()) {
			ERROR_PAGE_LOG.debug("a technical error occured [uuid: " + uuidString + "], IP Address: "
					+ request.getRemoteAddr() + ". " + exception.getMessage(), exception);
		}
		request.setAttribute("uuid", uuidString);
		request.setAttribute("exception", exception);

		return FORWARD_PREFIX + "/notFound";
	}

}
