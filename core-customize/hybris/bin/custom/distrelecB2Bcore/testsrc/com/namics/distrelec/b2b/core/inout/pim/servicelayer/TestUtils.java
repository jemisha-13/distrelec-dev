/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.hybris.platform.catalog.enums.ClassificationAttributeVisibilityEnum;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.AbstractCategoryElementConverter;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimProductReferenceDto;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.ClassificationClassWrapper;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;

/**
 * Utilities for converter tests.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class TestUtils {

    public static ImportContext getImportContext() {
        return getImportContext(new ClassificationClassModel());
    }

    public static ImportContext getImportContext(final ClassificationClassModel rootClassificationClass) {
        return getImportContext(new ClassificationSystemVersionModel(), rootClassificationClass);
    }

    public static ImportContext getImportContext(final ClassificationSystemVersionModel classificationSystemVersion,
                                                 final ClassificationClassModel rootClassificationClass) {

        final ImportContext importContext = new ImportContext();
        importContext.setIgnoreRootAttributes(true);
        importContext.setClassificationSystemVersion(classificationSystemVersion);
        importContext.setRootClassificationClass(rootClassificationClass);

        importContext.setCategoryCodePrefix("cat-");
        importContext.setCategoryCodeSuffix("");
        importContext.setClassificationClassCodePrefix("class-");

        return importContext;
    }

    public static ImportContext getImportContextMocking(final ClassificationClassModel rootClassificationClass) {
        return getImportContextMocking(Mockito.mock(ClassificationSystemVersionModel.class), rootClassificationClass);
    }

    public static ImportContext getImportContextMocking(final ClassificationSystemVersionModel classificationSystemVersion,
            final ClassificationClassModel rootClassificationClass) {

        final ImportContext importContext = Mockito.mock(ImportContext.class);
        Mockito.when(importContext.isIgnoreRootAttributes()).thenReturn(true);
        Mockito.when(importContext.getClassificationSystemVersion()).thenReturn(classificationSystemVersion);
        final ClassificationClassWrapper mockedRootClassificationClassWrapper = Mockito.mock(ClassificationClassWrapper.class);
        Mockito.when(mockedRootClassificationClassWrapper.getClassificationClass()).thenReturn(rootClassificationClass);
        Mockito.when(importContext.getRootClassificationClassWrapper()).thenReturn(mockedRootClassificationClassWrapper);
        importContext.setRootClassificationClass(rootClassificationClass);

        Mockito.when(importContext.getCategoryCodePrefix()).thenReturn(AbstractCategoryElementConverter.CATEGORY_ID_PREFIX);
        Mockito.when(importContext.getCategoryCodeSuffix()).thenReturn(AbstractCategoryElementConverter.CATEGORY_ID_SUFFIX);
        Mockito.when(importContext.getClassificationClassCodePrefix()).thenReturn("class-");

        final List<PimProductReferenceDto> list = new ArrayList<>();
        when(importContext.getProductReferenceDtos()).thenReturn(list);

        final ClassificationClassWrapper mockedClassificationClassWrapper = Mockito.mock(ClassificationClassWrapper.class);
        Mockito.when(importContext.getCurrentClassificationClassWrapper()).thenReturn(mockedClassificationClassWrapper);
        final ClassAttributeAssignmentModel mockedClassAttributeAssignment = createMockClassAttributeAssignmentModel();
        Mockito.when(mockedClassificationClassWrapper.getClassAttributeAssignment(Mockito.anyString())).thenReturn(mockedClassAttributeAssignment);

        return importContext;
    }

    public static ClassAttributeAssignmentModel createMockClassAttributeAssignmentModel() {
        final ClassAttributeAssignmentModel mockedClassAttributeAssignment = Mockito.mock(ClassAttributeAssignmentModel.class);
        final ClassificationClassModel mockedCassificationClass = Mockito.mock(ClassificationClassModel.class);
        Mockito.when(mockedCassificationClass.getCode()).thenReturn("mockedClassificationClassCode");
        Mockito.when(mockedClassAttributeAssignment.getClassificationClass()).thenReturn(mockedCassificationClass);
        final ClassificationAttributeModel mockedClassificationAttribute = Mockito.mock(ClassificationAttributeModel.class);
        Mockito.when(mockedClassificationAttribute.getCode()).thenReturn("mockedClassificationAttributeCode");
        Mockito.when(mockedClassAttributeAssignment.getClassificationAttribute()).thenReturn(mockedClassificationAttribute);
        Mockito.when(mockedClassAttributeAssignment.getVisibility()).thenReturn(ClassificationAttributeVisibilityEnum.A_VISIBILITY);
        return mockedClassAttributeAssignment;
    }

    public static void mockUnitForClassificationAttribute(final ClassificationSystemService mockedClassificationSystemService, final String unitCode,
            final String unitName) {
        final ClassificationAttributeUnitModel classificationUnit = mock(ClassificationAttributeUnitModel.class);

        classificationUnit.setCode(unitCode);
        classificationUnit.setSymbol(unitName);
        when(classificationUnit.getName(Mockito.<Locale> anyObject())).thenReturn(unitName);
        when(mockedClassificationSystemService.getAttributeUnitForCode(Mockito.<ClassificationSystemVersionModel> anyObject(), Mockito.eq(unitCode)))
                .thenReturn(classificationUnit);

    }

    /**
     * Checks that 2 lists have the same elements, using a custom comparator to check the list elements. Order matters
     * 
     * @param list1
     * @param list2
     * @param comparator
     * @return true if elements are the same, false otherwise.
     * @throws NullPointerException
     *             if any argument is null
     */
    public static boolean compareListsWithCustomComparator(final List list1, final List list2, final Comparator comparator) {
        Objects.requireNonNull(list1);
        Objects.requireNonNull(list2);
        Objects.requireNonNull(comparator);
        if (list1.size() != list2.size()) {
            return false;
        }
        return IntStream.range(0, list1.size()).allMatch(i -> comparator.compare(list1.get(i), list2.get(i)) == 0);
    }

    /**
     * Loads a resource linked to using {@clazz} using {@code resourcePath}, and transforms it to a {@link Element}
     * 
     * @param clazz
     *            the class to which the resource is connected
     * @param resourcePath
     *            the path of the file
     * @return a {@link Element}
     * @throws DocumentException
     */
    public static Element getDomElementFromResource(final Class clazz, final String resourcePath) throws DocumentException {
        final SAXReader reader = new SAXReader();
        final Document document = reader.read(clazz.getResourceAsStream(resourcePath));
        return document.getRootElement();
    }

    public static Date getDate(final int year, final int monthOneBase, final int day) {
        final Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, monthOneBase - 1, day);
        return cal.getTime();
    }

    /**
     * creates a {@link MockHttpServletRequest} and sets both its queryString and its internal parameters map
     * 
     * @param query
     * @return a {@link MockHttpServletRequest} with a filled queryString and parameters map
     */
    public static MockHttpServletRequest createMockHttpServletRequest(final String query) {
        return createMockHttpServletRequest("https", "example.com", "/", query);
    }

    /**
     * creates a {@link MockHttpServletRequest} and sets both its queryString and its internal parameters map
     * 
     * @param scheme
     * @param serverName
     * @param path
     * @param query
     * @return a {@link MockHttpServletRequest} with a filled queryString and parameters map
     */
    public static MockHttpServletRequest createMockHttpServletRequest(final String scheme, final String serverName, final String path, final String query) {
        final MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.GET.name(), path);
        request.setScheme(scheme);
        request.setServerName(serverName);
        request.setQueryString(query);
        final MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUriString(path + "?" + query).build().getQueryParams();
        request.setParameters(
                parameters.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().toArray(new String[e.getValue().size()]))));
        return request;
    }

}
