
package com.namics.distrelec.b2b.core.bmecat.export.query;

import java.util.Map;

/**
 * 
 * 
 * @author Abhinay Jadhav, Datwyler IT
 * @since 10-Dec-2017
 * 
 */
public interface DistBMECatParameterProvider<T> {

    Map<String, Object> getParameters(T configParameter);

}
