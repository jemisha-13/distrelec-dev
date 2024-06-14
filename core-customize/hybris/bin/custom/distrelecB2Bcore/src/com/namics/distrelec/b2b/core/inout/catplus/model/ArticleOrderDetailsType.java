//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.12.19 at 06:08:48 PM CET 
//


package com.namics.distrelec.b2b.core.inout.catplus.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ARTICLE_ORDER_DETAILSType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ARTICLE_ORDER_DETAILSType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ORDER_UNIT">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="C62"/>
 *               &lt;enumeration value="RO"/>
 *               &lt;enumeration value="MTR"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CONTENT_UNIT">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="C62"/>
 *               &lt;enumeration value="RO"/>
 *               &lt;enumeration value="MTR"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="NO_CU_PER_OU" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="PRICE_QUANTITY">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}float">
 *               &lt;enumeration value="1.0"/>
 *               &lt;enumeration value="100.0"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="QUANTITY_MIN" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="QUANTITY_INTERVAL" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ARTICLE_ORDER_DETAILSType", propOrder = {
 "orderUnit", "contentUnit", "nocuperou", "priceQuantity", "quantityMin", "quantityInterval"
})
public class ArticleOrderDetailsType {

    @XmlElement(name = "ORDER_UNIT", required = true)
    protected String orderUnit;
    @XmlElement(name = "CONTENT_UNIT", required = true)
    protected String contentUnit;
    @XmlElement(name = "NO_CU_PER_OU")
    protected double nocuperou;
    @XmlElement(name = "PRICE_QUANTITY")
    protected double priceQuantity;
    @XmlElement(name = "QUANTITY_MIN")
    protected int quantityMin;
    @XmlElement(name = "QUANTITY_INTERVAL")
    protected int quantityInterval;

    /**
     * Gets the value of the orderUnit property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getOrderUnit() {
        return orderUnit;
    }

    /**
     * Sets the value of the orderUnit property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setOrderUnit(final String value) {
        this.orderUnit = value;
    }

    /**
     * Gets the value of the contentUnit property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getContentUnit() {
        return contentUnit;
    }

    /**
     * Sets the value of the contentUnit property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setContentUnit(final String value) {
        this.contentUnit = value;
    }

    /**
     * Gets the value of the nocuperou property.
     * 
     */
    public double getNocuperou() {
        return nocuperou;
    }

    /**
     * Sets the value of the nocuperou property.
     * 
     */
    public void setNocuperou(final double value) {
        this.nocuperou = value;
    }

    /**
     * Gets the value of the priceQuantity property.
     * 
     */
    public double getPriceQuantity() {
        return priceQuantity;
    }

    /**
     * Sets the value of the priceQuantity property.
     * 
     */
    public void setPriceQuantity(final double value) {
        this.priceQuantity = value;
    }

    /**
     * Gets the value of the quantityMin property.
     * 
     */
    public int getQuantityMin() {
        return quantityMin;
    }

    /**
     * Sets the value of the quantityMin property.
     * 
     */
    public void setQuantityMin(final int value) {
        this.quantityMin = value;
    }

    /**
     * Gets the value of the quantityInterval property.
     * 
     */
    public int getQuantityInterval() {
        return quantityInterval;
    }

    /**
     * Sets the value of the quantityInterval property.
     * 
     */
    public void setQuantityInterval(final int value) {
        this.quantityInterval = value;
    }

}
