package com.namics.distrelec.b2b.storefront.etag;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;

public interface ETagCalculationService {

    String calculateProductPageETag(String productCode) throws CMSItemNotFoundException;

    String calculateContentPageETag(ContentPageModel contentPage) throws CMSItemNotFoundException;

}
