/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.storefront.controllers.cms;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.namics.distrelec.b2b.core.enums.CMSNavigationType;
import com.namics.distrelec.b2b.core.model.cms2.components.DistMainNavigationComponentModel;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;

import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;

/**
 * {@code DistMainNavigationComponentController}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.14
 */
@Scope("tenant")
@Controller("DistMainNavigationComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistMainNavigationComponent)
public class DistMainNavigationComponentController extends AbstractDistCMSComponentController<DistMainNavigationComponentModel> {

    private static final Logger LOG = Logger.getLogger(DistMainNavigationComponentController.class);
    private static final String MAIN_CATEGORY_NAVIGATION = "Main Category Navigation";

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.storefront.controllers.cms.AbstractCMSComponentController#fillModel(javax.servlet.http.HttpServletRequest,
     * org.springframework.ui.Model, de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel)
     */
    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistMainNavigationComponentModel component) {
        // DISTRELEC-11130 as first step, we limit the sorting to only category navigation type.
        Supplier<List<CMSNavigationNodeModel>> nodesSupplier;
        if (component != null && component.getNavigationType() == CMSNavigationType.CATEGORY_NAV) {
            nodesSupplier = () -> sortChildren(component.getRootNavigationNode(), getComparator());
        } else {
            nodesSupplier = () -> component.getRootNavigationNode().getChildren();
        }
        model.addAttribute("nodes", nodesSupplier); // provides supplier to allow lazy loading of navigation nodes
        model.addAttribute("DistBannerComponentsList", component.getDistBannerComponentsList());
    }

    /**
     * Sort children nodes based on the {@code sortingNumber}. Sort Main category by title.
     * "New Products" stays at the top and "Latest Offers" at the bottom of the list.
     * 
     * @param parent
     *            the parent navigation node
     * @param comparator
     *            the comparator to use for the comparison.
     */
    private List<CMSNavigationNodeModel> sortChildren(final CMSNavigationNodeModel parent, final Comparator<CMSNavigationNodeModel> comparator) {
        if (parent != null && CollectionUtils.isNotEmpty(parent.getChildren())) {
            final List<CMSNavigationNodeModel> children = parent.getChildren().stream()
                                                                              .filter(CMSNavigationNodeModel::isVisible)
                                                                              .sorted(getComparator())
                                                                              .collect(Collectors.toList());
            if(MAIN_CATEGORY_NAVIGATION.equals(parent.getTitle())) {
                sortByTitle(children);
            } else {
                Collections.sort(children, comparator);
            }
            parent.setChildren(children);
            for (final CMSNavigationNodeModel child : children) {
                sortChildren(child, comparator);
            }
            return children;
        }
        return null;
    }

    private void sortByTitle(List<CMSNavigationNodeModel> children) {
        Comparator<CMSNavigationNodeModel> compareByTitle = Comparator.comparing(CMSNavigationNodeModel::getTitle);
        if (children.size() > 3) {
            Collections.sort(children.subList(0, children.size() - 3), compareByTitle);
        } else {
            Collections.sort(children.subList(0, children.size()), compareByTitle);
        }
    }

    /**
     * @return the default comparator of {@code CMSNavigationNodeModel}
     */
    private static Comparator<CMSNavigationNodeModel> getComparator() {
        return (o1, o2) -> {
            final String sn1 = o1.getSortingNumber();
            final String sn2 = o2.getSortingNumber();
            if (sn1 == null && sn2 == null) {
                return 0;
            } else if (sn1 == null && sn2 != null) {
                return -1;
            } else if (sn1 != null && sn2 == null) {
                return 1;
            } else {
                return sn1.compareTo(sn2);
            }
        };
    }
}
