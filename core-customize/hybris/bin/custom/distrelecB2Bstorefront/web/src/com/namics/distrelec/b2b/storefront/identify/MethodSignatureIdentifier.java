/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.identify;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.log4j.Logger;
import org.springframework.util.ClassUtils;

import javax.servlet.ServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class MethodSignatureIdentifier {

    private final static Logger LOG = Logger.getLogger(MethodSignatureIdentifier.class.getName());

    private Integer maxDepth = Integer.valueOf(2);

    /**
     * Returns a Map<String, Object> where the key is the Method name and the Method type is the value.
     *
     * @param request
     * @return
     */
    public Map<String, Object> identifyFromRequest(final ServletRequest request) {
        return getAllMethodSignaturesFromRequestScope(request);
    }

    /**
     * Returns either a Map<String, Object> or a Collection of Map<String, Object>, depending on what type the passed object has. The Map
     * contains the Method name as the key and the Method Type and Return value as the value.
     *
     * @param object
     * @return
     */
    public Object identify(final Object object) {
        if (null == object) {
            throw new NullArgumentException("Passed argument 'object' should not be null");
        }

        if (isCollection(object)) {
            return getMethodSignaturesFromCollection(object, 1);
        }

        return getMethodSignaturesFromObject(object, 1);
    }

    /**
     * Collects a map of a relevant attributes with the corresponding attribute type within the request scope.
     *
     * <pre>
     * ...
     * languages:  java.util.ArrayList
     * lastLogin:  java.util.Date
     * pageTitle:  java.lang.String
     * ...
     * </pre>
     *
     * @return
     */
    private Map<String, Object> getAllMethodSignaturesFromRequestScope(final ServletRequest request) {
        final Map<String, Object> methods = new HashMap<String, Object>();
        @SuppressWarnings("unchecked")
        final Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final String attributeName = attributeNames.nextElement();
            final Object object = request.getAttribute(attributeName);
            if (isCollection(object)) {
                methods.put(attributeName, getMethodSignaturesFromCollection(object, 1));
            } else {
                methods.put(attributeName, getMethodSignaturesFromObject(object, 1));
            }
        }

        return methods;
    }

    /**
     * Collects a map of relevant methods of a certain object. The map value is the return value of that method.
     *
     * @param object
     * @param depth
     * @return
     */
    private Map<String, Object> getMethodSignaturesFromObject(final Object object, final int depth) {
        final Map<String, Object> relevantMethods = new HashMap<String, Object>();

        final Method[] methods = object.getClass().getMethods();
        for (final Method method : methods) {
            if (isRelevantMethodForFrontend(method)) {
                relevantMethods.put(method.getName(), getReturnvalueAndTypeFromMethodOfObject(object, method, depth));
            }
        }
        return relevantMethods;
    }

    /**
     * Create a MethodSignature with the return type of the method and the return value.
     *
     * @param object
     * @param method
     * @param depth
     * @return
     */
    private Object getReturnvalueAndTypeFromMethodOfObject(final Object object, final Method method, final int depth) {
        final MethodSignature identifierMethod = new MethodSignature();
        identifierMethod.setReturnType(method.getReturnType().toString());
        identifierMethod.setReturnValue(getReturnValue(object, method, depth));
        return identifierMethod;
    }

    /**
     * Gets the return value for a certain method call on a certain object. The return value can be ...
     *
     * <pre>
     * 1.) a collection (recursive)
     * 2.) a POJO object (recursive)
     * 3.) a primitive
     * </pre>
     *
     * @param object
     * @param method
     * @param depth
     * @return
     */
    private Object getReturnValue(final Object object, final Method method, final int depth) {
        Object returnValue;
        try {
            returnValue = method.invoke(object, (Object[]) null);

            if (isCollection(returnValue) && depth < this.maxDepth.intValue()) {
                returnValue = getMethodSignaturesFromCollection(returnValue, depth + 1);
            } else if (isNotPrimitiveOrWrapper(returnValue) && depth < this.maxDepth.intValue() && !String.class.equals(returnValue.getClass())) {
                returnValue = getMethodSignaturesFromObject(returnValue, depth + 1);
            } else if (null != returnValue) {
                returnValue = returnValue.toString();
            }
        } catch (final IllegalAccessException e) {
            LOG.debug("Cannot execute method " + method.getName() + " on class " + object.getClass().getName() + " without any arguments");
            returnValue = "<undefined value>";
        } catch (final IllegalArgumentException e1) {
            LOG.debug("Cannot execute method " + method.getName() + " on class " + object.getClass().getName() + " without any arguments");
            returnValue = "<undefined value>";
        } catch (final InvocationTargetException e2) {
            LOG.debug("Cannot execute method " + method.getName() + " on class " + object.getClass().getName() + " without any arguments");
            returnValue = "<undefined value>";
        }

        return returnValue;
    }

    /**
     * Instead of getting all relevant methods of a certain object that seems to be a collection, the collection will be iterated and the
     * included objects will be used to get the relevant methods. The resulting list of relevant methods will be returned.
     *
     * @param object
     * @param depth
     * @return
     */
    private List<Map<String, Object>> getMethodSignaturesFromCollection(final Object object, final int depth) {
        if (!(object instanceof Collection)) {
            throw new IllegalArgumentException("Passed argument '" + object + "' should be of type 'Collection'");
        }

        @SuppressWarnings("unchecked")
        final Collection<Object> collection = (Collection<Object>) object;
        final List<Map<String, Object>> methods = new ArrayList<Map<String, Object>>();

        if (CollectionUtils.isEmpty(collection)) {
            return methods;
        } else {
            for (final Object collectionEntry : collection) {
                if (null != collectionEntry) {
                    methods.add(getMethodSignaturesFromObject(collectionEntry, depth));
                }
            }
        }

        return methods;
    }

    private boolean isNotPrimitiveOrWrapper(final Object object) {
        return null != object && !ClassUtils.isPrimitiveOrWrapper(object.getClass());
    }

    private boolean isRelevantMethodForFrontend(final Method method) {
        return methodTakesNoArguments(method) && method.getName().startsWith("get") || method.getName().startsWith("is");
    }

    private boolean methodTakesNoArguments(final Method method) {
        return method.getParameterTypes().length == 0;
    }

    private boolean isCollection(final Object possibleCollection) {
        return null != possibleCollection && Collection.class.isAssignableFrom(possibleCollection.getClass());
    }

    public void setMaxDepth(final Integer maxDepth) {
        this.maxDepth = maxDepth;
    }
}
