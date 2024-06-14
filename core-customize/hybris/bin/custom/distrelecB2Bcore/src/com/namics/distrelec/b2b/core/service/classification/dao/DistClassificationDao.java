/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.classification.dao;

import java.util.List;
import java.util.Locale;

import com.distrelec.b2b.core.search.data.Unit;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.daos.ClassificationDao;

/**
 * {@code DistClassificationDao}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.10
 */
public interface DistClassificationDao extends ClassificationDao {

    ClassificationAttributeModel findClassificationAttribute(final String code);

	ClassificationAttributeValueModel getClassificationAttributeValue(ClassificationAttributeModel classificationAttributeModel,
																	  String code);

	List<ClassificationAttributeModel> findClassificationAttribute(String classAttrName, Locale locale);

	List<ClassificationAttributeValueModel> findClassificationAttributeValue(List<ClassificationAttributeModel> classAttrs,
																			 String classAttrValueName, Locale locale);

	List<Unit> findAttributeUnitsWithUnitGroup();

	ClassificationAttributeUnitModel findBaseUnitForUnit(ClassificationAttributeUnitModel unit);
}
