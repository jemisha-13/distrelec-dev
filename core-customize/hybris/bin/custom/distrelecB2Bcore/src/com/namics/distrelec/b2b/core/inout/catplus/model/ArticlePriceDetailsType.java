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
 * <p>Java class for ARTICLE_PRICE_DETAILSType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ARTICLE_PRICE_DETAILSType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DATETIME" type="{}DATETIMEType"/>
 *         &lt;element name="DAILY_PRICE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ARTICLE_PRICE" type="{}ARTICLE_PRICEType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ARTICLE_PRICE_DETAILSType", propOrder = {
 "dateTime", "dailyPrice", "articlePrice"
})
public class ArticlePriceDetailsType {

    @XmlElement(name = "DATETIME")
    protected DateTimeType dateTime;
    @XmlElement(name = "DAILY_PRICE")
    protected String dailyPrice;
    @XmlElement(name = "ARTICLE_PRICE")
    protected List<ArticlePriceType> articlePrice;

    /**
     * Gets the value of the dateTime property.
     * 
     * @return possible object is {@link DateTimeType }
     * 
     */
    public DateTimeType getDateTime() {
        return dateTime;
    }

    /**
     * Sets the value of the dateTime property.
     * 
     * @param value
     *            allowed object is {@link DateTimeType }
     * 
     */
    public void setDateTime(final DateTimeType value) {
        this.dateTime = value;
    }

    /**
     * Gets the value of the dailyPrice property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getDailyPrice() {
        return dailyPrice;
    }

    /**
     * Sets the value of the dailyPrice property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setDailyPrice(final String value) {
        this.dailyPrice = value;
    }

    /**
     * Gets the value of the articlePrice property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
     * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the articleprice property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getARTICLEPRICE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link ArticlePriceType }
     * 
     * 
     */
    public List<ArticlePriceType> getArticlePrice() {
        if (articlePrice == null) {
            articlePrice = new ArrayList<ArticlePriceType>();
        }
        return this.articlePrice;
    }

}
