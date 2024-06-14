package com.namics.distrelec.b2b.facades.cms.converters;

import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.cms2.components.DistFooterComponentModel;
import com.namics.distrelec.b2b.core.model.cms2.components.DistFooterItemComponentModel;
import com.namics.distrelec.b2b.core.model.cms2.components.DistPaymentMethodComponentModel;
import com.namics.distrelec.b2b.facades.cms.data.DistExternalLinkData;
import com.namics.distrelec.b2b.facades.cms.data.DistFooterComponentData;
import com.namics.distrelec.b2b.facades.cms.data.DistFooterComponentItemData;

import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DistFooterComponentConverter extends AbstractPopulatingConverter<DistFooterComponentModel, DistFooterComponentData> {

    @Autowired
    @Qualifier("defaultDistImageConverter")
    private Converter<MediaModel, ImageData> imageConverter;

    @Override
    public void populate(final DistFooterComponentModel source, final DistFooterComponentData target) {
        super.populate(source, target);
        target.setUid(source.getUid());
        target.setNavigationNodes(getVisibleNavigationNodes(source));
        target.setImpressumLinks(source.getImpressumLinks());
        target.setCountryLinks(source.getCountryLinks());
        target.setNotice(source.getNotice());
        target.setName(source.getName());
        target.setCheckoutFooter(BooleanUtils.isTrue(source.getCheckout()));
        target.setWrapAfter(source.getWrapAfter() != null ? source.getWrapAfter() : 0);
        target.setUSPs(createItems(source.getUsps()));
        target.setAddedValues(createItems(source.getAddedValues()));
        target.setSocialMedias(getPaymentMethodIcons(source.getSocialMedias()));
        target.setPaymentMethods(getPaymentMethodIcons(source.getPaymentMethods()));
        target.setTrademarks(getPaymentMethodIcons(source.getTrademarks()));
    }

    private List<CMSNavigationNodeModel> getVisibleNavigationNodes(DistFooterComponentModel source) {
        if (nonNull(source) && nonNull(source.getNavigationNode())) {
            return source.getNavigationNode().getChildren().stream()
                         .filter(Objects::nonNull)
                         .filter(CMSNavigationNodeModel::isVisible)
                         .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<DistFooterComponentItemData> createItems(final List<DistFooterItemComponentModel> footerItems) {
        return emptyIfNull(footerItems).stream()
                                       .map(this::convertFooterItemComponent)
                                       .collect(Collectors.toList());
    }

    private DistFooterComponentItemData convertFooterItemComponent(DistFooterItemComponentModel footerItemComponent) {
        DistFooterComponentItemData itemData = new DistFooterComponentItemData();
        itemData.setText(footerItemComponent.getText());
        itemData.setIcon(footerItemComponent.getIcon() != null ? imageConverter.convert(footerItemComponent.getIcon()) : null);
        return itemData;
    }

    private List<DistExternalLinkData> getPaymentMethodIcons(final List<DistPaymentMethodComponentModel> paymentMethods) {
        return emptyIfNull(paymentMethods).stream()
                                          .map(this::convertPaymentMethodComponent)
                                          .collect(Collectors.toList());
    }

    private DistExternalLinkData convertPaymentMethodComponent(DistPaymentMethodComponentModel payMethodComponent) {
        return new DistExternalLinkData(payMethodComponent.getIcon() != null ? imageConverter.convert(payMethodComponent.getIcon()) : null,
                                        payMethodComponent.getLocalizedUrl(),
                                        payMethodComponent.getTitle());
    }
}
