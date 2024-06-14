package com.distrelec.smartedit.predicates;

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.cmsitems.predicates.CMSItemNameExistsPredicate;

import java.util.function.Predicate;

public class DistrelecCMSItemNameExistsPredicate extends CMSItemNameExistsPredicate implements Predicate<CMSItemModel> {

    @Override
    public boolean test(CMSItemModel cmsItem) {
        if (cmsItem instanceof AbstractPageModel) {
            return false;
        }

        return super.test(cmsItem);
    }
}
