package com.namics.distrelec.b2b.storefront.controllers.cms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.namics.distrelec.b2b.core.model.cms2.components.DistFeaturedProductsComponentModel;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;

import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.core.model.product.ProductModel;

@Controller("DistFeaturedProductsComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistFeaturedProductsComponent)
public class DistFeaturedProductsComponentController extends AbstractDistCMSComponentController<DistFeaturedProductsComponentModel> {

    @Autowired
    private ConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator;

    @Autowired
    @Qualifier("productService")
    private DistProductService productService;

    @Override
    protected void fillModel(HttpServletRequest request, Model model, DistFeaturedProductsComponentModel component) {
        List<ProductData> productDataList = new ArrayList<>(component.getProducts().size());
        Map<ProductData, ProductModel> data2model = new HashMap<>();
        for (ProductModel productModel : component.getProducts()) {
            if (getProductService().isProductBuyable(productModel)) {
                ProductData productData = convertProduct(productModel);

                data2model.put(productData, productModel);
                productDataList.add(productData);
            }
        }

        model.addAttribute("dataAttributes", component.getDataAttributes());
        model.addAttribute("sectionTitle", component.getSectionTitle());
        model.addAttribute("linkText", component.getLinkText());
        model.addAttribute("products", productDataList);
        model.addAttribute("data2modelMap", data2model);
    }

    private ProductData convertProduct(ProductModel productModel) {
        ProductData productData = new ProductData();
        productConfiguredPopulator.populate(productModel, productData, Arrays.asList(
                                                                                     ProductOption.BASIC,
                                                                                     ProductOption.DESCRIPTION,
                                                                                     ProductOption.PRICE,
                                                                                     ProductOption.PROMOTION_LABELS,
                                                                                     ProductOption.URL,
                                                                                     ProductOption.DIST_MANUFACTURER));
        return productData;
    }

    public DistProductService getProductService() {
        return productService;
    }

    public void setProductService(DistProductService productService) {
        this.productService = productService;
    }
}
