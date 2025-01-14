//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.12.19 at 06:08:48 PM CET 
//


package com.namics.distrelec.b2b.core.inout.catplus.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ADDRESSType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ADDRESSType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="STREET" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ZIP" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="CITY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="STATE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="COUNTRY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ADDRESS_REMARKS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ADDRESSType", propOrder = {
    "name",
    "street",
    "zipCode",
    "city",
    "state",
    "country",
    "addressRemarks"
})
public class AddressType {

    @XmlElement(name = "NAME", required = true)
    protected String name;
    @XmlElement(name = "STREET", required = true)
    protected String street;
    @XmlElement(name = "ZIP")
    protected int zipCode;
    @XmlElement(name = "CITY", required = true)
    protected String city;
    @XmlElement(name = "STATE", required = true)
    protected String state;
    @XmlElement(name = "COUNTRY", required = true)
    protected String country;
    @XmlElement(name = "ADDRESS_REMARKS", required = true)
    protected String addressRemarks;
    @XmlAttribute(name = "type")
    protected String type;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(final String value) {
        this.name = value;
    }

    /**
     * Gets the value of the street property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the value of the street property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreet(final String value) {
        this.street = value;
    }

    /**
     * Gets the value of the zip property.
     * 
     */
    public int getZipCode() {
        return zipCode;
    }

    /**
     * Sets the value of the zipCode property.
     * 
     */
    public void setZipCode(final int value) {
        this.zipCode = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(final String value) {
        this.city = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setState(final String value) {
        this.state = value;
    }

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountry(final String value) {
        this.country = value;
    }

    /**
     * Gets the value of the addressRemarks property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getAddressRemarks() {
        return addressRemarks;
    }

    /**
     * Sets the value of the addressRemarks property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setAddressRemarks(final String value) {
        this.addressRemarks = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(final String value) {
        this.type = value;
    }

}
