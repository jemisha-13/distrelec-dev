/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.export.impl;

import com.namics.distrelec.b2b.core.service.export.data.AbstractDistExportData;
import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.ConvertObjectsToCSV.*;
import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFile;
import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFileMethod;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

import static de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.ConvertObjectsToCSV.*;

/**
 * Extend hybris default conversion from objects to string by adding special behavior for volume prices
 */
public class DistConvertObjectsToCSV<T extends AbstractDistExportData> implements Converter<List<T>, String> {

    private static final Logger LOG = Logger.getLogger(DistConvertObjectsToCSV.class);

    @Override
    public String convert(final List<T> dataList, String arg1) throws ConversionException {
        return convert(dataList);
    }

    @Override
    public String convert(final List<T> dataList) {
        final List<String> result = new ArrayList<String>();
        if (!dataList.isEmpty()) {
            final int maxNumberVolumePrices = getMaxNumberOfVolumePrices(dataList);
            final ClassAnnotationProperties classProperties = buildClassProperties(dataList.get(0));

            final String header = generateHeader(classProperties);
            final String headerVolumePrices = generateHeaderVolumePrices(classProperties.getDelimiter(), maxNumberVolumePrices);
            result.add(StringUtils.replace(header, ";VolumePrices", headerVolumePrices));
            LOG.info("Generated header: " + result.get(0));

            result.addAll(generateData(dataList, classProperties));
        }
        final StringBuilder builder = new StringBuilder();
        for (final String s : result) {
            builder.append(s).append("\r\n");
        }
        return builder.toString();
    }

    protected List<String> generateData(final List<T> dataList, final ClassAnnotationProperties classProperties) {
        final List<String> result = new ArrayList<String>();
        final int maxNumberVolumePrices = getMaxNumberOfVolumePrices(dataList);
        for (final Object data : dataList) {
            final Map<Integer, Method> integerMethodMap = classProperties.getGetters();
            final Iterator<Integer> integerIterator = integerMethodMap.keySet().iterator();
            final StringBuilder builder = new StringBuilder();
            while (integerIterator.hasNext()) {
                final Integer integer = integerIterator.next();
                final Method method = integerMethodMap.get(integer);
                Object returnObject = ReflectionUtils.invokeMethod(method, data);
                if (returnObject == null) {
                    returnObject = classProperties.getNullValue().get(integer);
                }
                if (method.getName().equals("getVolumePrices")) {
                    returnObject = generateDataVolumePrices(returnObject, classProperties.getDelimiter(), maxNumberVolumePrices);
                    if (StringUtils.isBlank((String) returnObject)) {
                        builder.deleteCharAt(builder.length() - 1);
                    }
                }
                builder.append(returnObject);
                builder.append(integerIterator.hasNext() ? classProperties.getDelimiter() : "");
            }
            LOG.info(builder);
            result.add(builder.toString());
        }

        return result;
    }

    protected String generateHeaderVolumePrices(final String delimiter, final int maxNumberVolumePrices) {
        final StringBuilder headerVolumePrices = new StringBuilder();
        for (int i = 1; i <= maxNumberVolumePrices; i++) {
            headerVolumePrices.append(delimiter);
            headerVolumePrices.append("unit ");
            headerVolumePrices.append(i);
            headerVolumePrices.append(delimiter);
            headerVolumePrices.append("price ");
            headerVolumePrices.append(i);
        }

        return headerVolumePrices.toString();
    }

    protected String generateDataVolumePrices(final Object obj, final String delimiter, final int maxNumberVolumePrices) {
        int iCnt = 0;
        final StringBuilder dataVolumePrices = new StringBuilder();

        if (!(obj instanceof String)) {
            final Map<Long, String> volumePrices = (Map<Long, String>) obj;
            for (final Map.Entry<Long, String> volumePrice : volumePrices.entrySet()) {
                dataVolumePrices.append(volumePrice.getKey());
                dataVolumePrices.append(delimiter);
                dataVolumePrices.append(volumePrice.getValue());
                if (iCnt < volumePrices.size() - 1) {
                    dataVolumePrices.append(delimiter);
                }
                iCnt++;
            }
        }

        if (iCnt < maxNumberVolumePrices) {
            for (int i = iCnt; i < maxNumberVolumePrices; i++) {
                dataVolumePrices.append(delimiter); // unit
                dataVolumePrices.append(delimiter); // price
            }
        }

        return dataVolumePrices.toString();
    }

    protected int getMaxNumberOfVolumePrices(final List<? extends AbstractDistExportData> dataList) {
        int maxNumberVolumePrices = 0;
        for (final Object obj : dataList) {
            final AbstractDistExportData data = (AbstractDistExportData) obj;
            if (MapUtils.isNotEmpty(data.getVolumePrices())) {
                if (data.getVolumePrices().size() > maxNumberVolumePrices) {
                    maxNumberVolumePrices = data.getVolumePrices().size();
                }
            }
        }

        return maxNumberVolumePrices;
    }

    // use the getter/setter to generate the field name
    protected String workoutPropertyName(final String methodName) {
        final StringBuilder builder = new StringBuilder();
        if (methodName.startsWith(GETTER) || methodName.startsWith(SETTER) || methodName.startsWith(IS)) {
            final String newMethodName = methodName.startsWith(IS) ? methodName.substring(INDEX_TWO) : methodName.substring(INDEX_THREE);
            for (int j = 0; j < newMethodName.length(); j++) {
                if ((Character.isUpperCase(newMethodName.charAt(j)) || Character.isDigit(newMethodName.charAt(j))) && j != 0) {
                    builder.append(' ');
                }
                builder.append(Character.toLowerCase(newMethodName.charAt(j)));
            }
        }

        return builder.toString();
    }

    /**
     * Method that gathers information about the data class and places the information into a ClassAnnotationProperties object.
     *
     * @param data
     * @return classProperties
     */
    protected ClassAnnotationProperties buildClassProperties(final Object data) {
        final Map<Integer, String> fields = new HashMap<Integer, String>();
        final Map<Integer, Method> getters = new HashMap<Integer, Method>();
        final Map<Integer, Method> setters = new HashMap<Integer, Method>();
        final Map<Integer, String> nullValues = new HashMap<Integer, String>();

        final Class<?> aClass = data.getClass();
        final Method[] methods = aClass.getMethods();

        for (final Method method : methods) {
            final DelimitedFileMethod annotation = method.getAnnotation(DelimitedFileMethod.class);
            if (annotation != null) {
                final int position = annotation.position();
                final String methodName = method.getName();

                String name = DistUtils.localizeText(annotation.name());

                if ("".equals(name)) {
                    name = workoutPropertyName(methodName);
                }
                fields.put(Integer.valueOf(position), name);

                if (methodName.startsWith(GETTER) || methodName.startsWith(IS)) {
                    getters.put(Integer.valueOf(position), method);
                    setters.put(Integer.valueOf(position), ReflectionUtils.findMethod(data.getClass(),
                            SETTER + (methodName.startsWith(GETTER) ? methodName.substring(INDEX_THREE) : methodName.substring(INDEX_TWO))));
                    nullValues.put(Integer.valueOf(position), annotation.nullValue());
                } else if (methodName.startsWith(SETTER)) {
                    setters.put(Integer.valueOf(position), method);
                    getters.put(Integer.valueOf(position), ReflectionUtils.findMethod(data.getClass(), GETTER + methodName.substring(INDEX_THREE)));
                    nullValues.put(Integer.valueOf(position), annotation.nullValue());
                }

            }
        }

        final DelimitedFile delimitedFile = aClass.getAnnotation(DelimitedFile.class);
        final String delimiter = delimitedFile.delimiter();

        return new ClassAnnotationProperties(delimiter, fields, getters, setters, nullValues);
    }

    protected String generateHeader(final ClassAnnotationProperties dataProperties) {
        final StringBuilder builder = new StringBuilder();
        final Map<Integer, String> map = dataProperties.getPropertyNames();
        final Iterator<Entry<Integer, String>> entriesIterator = map.entrySet().iterator();

        while (entriesIterator.hasNext()) {
            final Entry<Integer, String> currentEntry = entriesIterator.next();
            final String dataProperty = currentEntry.getValue();
            builder.append(dataProperty);
            if (entriesIterator.hasNext()) {
                builder.append(dataProperties.getDelimiter());
            }
        }

        return builder.toString();
    }
}
