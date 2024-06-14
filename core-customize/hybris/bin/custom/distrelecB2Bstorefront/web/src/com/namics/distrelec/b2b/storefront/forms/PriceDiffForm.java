/**
 * 
 */
package com.namics.distrelec.b2b.storefront.forms;

import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * {@code PriceDiffForm}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.8
 */
public class PriceDiffForm implements Serializable {

    private String source;
    private String target;

    @NotBlank
    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    @NotBlank
    public String getTarget() {
        return target;
    }

    public void setTarget(final String target) {
        this.target = target;
    }
}
