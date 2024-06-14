package com.namics.distrelec.b2b.storefront.controllers.cms;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.namics.distrelec.b2b.core.model.cms2.components.DistProductCardComponentModel;
import com.namics.distrelec.b2b.facades.cms.data.DistProductCardComponentData;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;

@Controller("DistProductCardComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistProductCardComponent)
public class DistProductCardComponentController extends AbstractDistCMSComponentController<DistProductCardComponentModel> {
    
    @Override
    protected void fillModel(HttpServletRequest request, Model model, DistProductCardComponentModel component) {
        DistProductCardComponentData data = createProductCardData(component);
        model.addAttribute("productBoxData", data);
    }
    
    protected DistProductCardComponentData createProductCardData(final DistProductCardComponentModel component) {
        final DistProductCardComponentData data = new DistProductCardComponentData();
        if (component.getOrientation() != null) {
            data.setOrientation(component.getOrientation().name());
        }
        data.setArticleNumber(component.getArticleNumber());
        data.setShowImage(component.getImage());
        data.setShowSnippet(component.getSnippet());
        data.setShowPrice(component.getPrice());
        data.setShowTitle(component.getTitle());
        data.setCustomTitle(component.getCustomTitle());
        if (component.getButtonType() != null) {
            data.setButtonType(component.getButtonType().name());
        }
        data.setCustomDescription(component.getCustomDescription());
        data.setBrandLogoAlternateText(component.getBrandAlternateText());
        data.setShowBrandLogo(component.getBrandLogo());
        data.setArticleToggle(component.getArticleToggle());
        data.setShowStarRating(component.getStarRating());
        data.setShowAvailability(component.getAvailability());
        data.setShowCompareCheckbox(component.getCompareCheckbox());
        data.setShowAddToList(component.getAddToList());
        data.setLabelDisplay(component.getLabelDisplay());
        data.setTopDisplay(component.getTopDisplay());
        data.setPromotionParameter(component.getPromotionParameter());        
        return data;
    }
    
   

}
