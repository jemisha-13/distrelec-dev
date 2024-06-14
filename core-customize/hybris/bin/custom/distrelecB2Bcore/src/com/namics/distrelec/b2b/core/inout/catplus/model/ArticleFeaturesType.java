//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.12.19 at 06:08:48 PM CET 
//


package com.namics.distrelec.b2b.core.inout.catplus.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ARTICLE_FEATURESType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ARTICLE_FEATURESType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="REFERENCE_FEATURE_SYSTEM_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REFERENCE_FEATURE_GROUP_ID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="FEATURE" type="{}FEATUREType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ARTICLE_FEATURESType", propOrder = {
 "referenceFeatureSystemName", "referenceFeatureGroupId", "featureList"
})
public class ArticleFeaturesType {


    @XmlElement(name = "REFERENCE_FEATURE_SYSTEM_NAME")
    protected String referenceFeatureSystemName;
    @XmlElement(name = "REFERENCE_FEATURE_GROUP_ID")
    protected String referenceFeatureGroupId;
    @XmlElement(name = "FEATURE")
    protected List<FeatureType> featureList;


    /**
     * Getter for the {@code referenceFeatureSystemName} property
     * 
     * @return the referenceFeatureSystemName
     */
    public String getReferenceFeatureSystemName() {
        return referenceFeatureSystemName;
    }


    /**
     * Setter for the {@code referenceFeatureSystemName} property
     * 
     * @param referenceFeatureSystemName
     *            the referenceFeatureSystemName to set
     */
    public void setReferenceFeatureSystemName(final String referenceFeatureSystemName) {
        this.referenceFeatureSystemName = referenceFeatureSystemName;
    }


    /**
     * Getter for the {@code referenceFeatureGroupId} property
     * 
     * @return the referenceFeatureGroupId
     */
    public String getReferenceFeatureGroupId() {
        return referenceFeatureGroupId;
    }


    /**
     * Setter for the {@code referenceFeatureGroupId} property
     * 
     * @param referenceFeatureGroupId
     *            the referenceFeatureGroupId to set
     */
    public void setReferenceFeatureGroupId(final String referenceFeatureGroupId) {
        this.referenceFeatureGroupId = referenceFeatureGroupId;
    }

    /**
     * Gets the value of the {@code featureList} property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
     * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the {@code featureList} property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getFeatureList().add(newItem);
     * </pre>
     * 
     * @return the featureList
     */
    public List<FeatureType> getFeatureList() {
        if (this.featureList == null) {
            this.featureList = new ArrayList<FeatureType>();
        }
        return featureList;
    }
}