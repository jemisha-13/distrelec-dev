<?xml version="1.0" encoding="UTF-8"?>

<!-- XSL.Generated_by -->
<!-- Please leave unchanged; manage layouts unstead -->
<!DOCTYPE xsl:stylesheet [
        <!ENTITY XML "http://www.w3.org/TR/REC-xml">
        <!ENTITY XMLNames "http://www.w3.org/TR/REC-xml-names">
        <!ENTITY XSLT.ns "http://www.w3.org/1999/XSL/Transform">
        <!ENTITY XSLTA.ns "http://www.w3.org/1999/XSL/TransformAlias">
        <!ENTITY XSLFO.ns "http://www.w3.org/1999/XSL/Format">
        <!ENTITY copy "&#169;">
        <!ENTITY trade "&#8482;">
        <!ENTITY deg "&#x00b0;">
        <!ENTITY gt "&#62;">
        <!ENTITY sup2 "&#x00b2;">
        <!ENTITY frac14 "&#x00bc;">
        <!ENTITY quot "&#34;">
        <!ENTITY frac12 "&#x00bd;">
        <!ENTITY euro "&#x20ac;">
        <!ENTITY Omega "&#937;">
        ]>

<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.1"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:saxon="http://icl.com/saxon"
                extension-element-prefixes="saxon">

    <xsl:template match="CART">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="masterNamePageMain0" page-height="845.0pt" page-width="598.0pt"
                                       margin-top="10.0pt" margin-left="10.0pt"
                                       margin-bottom="10.0pt" margin-right="10.0pt">
                    <fo:region-body margin-left="28.0pt" margin-top="94.0pt" margin-bottom="40.0pt"
                                    margin-right="28.0pt"/>
                    <fo:region-before extent="94.0pt" precedence="true"/>
                    <fo:region-after extent="87.0pt" precedence="true"/>
                    <fo:region-start extent="28.0pt" precedence="false"/>
                    <fo:region-end extent="28.0pt" precedence="false"/>
                </fo:simple-page-master>
                <fo:simple-page-master master-name="masterNamePageFirst1" page-height="845.0pt" page-width="598.0pt"
                                       margin-top="10.0pt" margin-left="10.0pt"
                                       margin-bottom="10.0pt" margin-right="10.0pt">
                    <fo:region-body margin-left="28.0pt" margin-top="165.0pt" margin-bottom="40.0pt"
                                    margin-right="28.0pt"/>
                    <fo:region-before region-name="regionBefore1" extent="493.0pt" precedence="true"/>
                    <fo:region-after region-name="regionAfter1" extent="99.0pt" precedence="true"/>
                    <fo:region-start region-name="regionStart1" extent="28.0pt" precedence="false"/>
                    <fo:region-end region-name="regionEnd1" extent="28.0pt" precedence="false"/>
                </fo:simple-page-master>
                <fo:simple-page-master master-name="masterNamePageRest2" page-height="845.0pt" page-width="598.0pt"
                                       margin-top="10.0pt" margin-left="10.0pt"
                                       margin-bottom="10.0pt" margin-right="10.0pt">
                    <fo:region-body margin-left="28.0pt" margin-top="40.0pt" margin-bottom="40.0pt"
                                    margin-right="28.0pt"/>
                    <fo:region-before region-name="regionBefore2" extent="126.0pt" precedence="true"/>
                    <fo:region-after region-name="regionAfter2" extent="99.0pt" precedence="true"/>
                    <fo:region-start region-name="regionStart2" extent="28.0pt" precedence="false"/>
                    <fo:region-end region-name="regionEnd2" extent="28.0pt" precedence="false"/>
                </fo:simple-page-master>
                <fo:page-sequence-master master-name="masterSequenceName1">
                    <fo:repeatable-page-master-alternatives>
                        <fo:conditional-page-master-reference master-reference="masterNamePageFirst1"
                                                              page-position="first"/>
                        <fo:conditional-page-master-reference master-reference="masterNamePageRest2"
                                                              page-position="rest"/>
                    </fo:repeatable-page-master-alternatives>
                </fo:page-sequence-master>
            </fo:layout-master-set>
            <fo:page-sequence master-name="masterNamePageMain0" master-reference="masterSequenceName1">
                <fo:static-content flow-name="regionBefore1">
                    <fo:block-container position="absolute" top="29.0pt" left="420.0pt" height="72.0pt"
                                        width="130.0pt">
                        <fo:block>
                            <fo:external-graphic content-height="scale-to-fit" height="72.0pt"
                                                 content-width="130.0pt">
                                <xsl:attribute name="src">url('<xsl:apply-templates select="HEADER/LOGO_URL"/>')
                                </xsl:attribute>
                            </fo:external-graphic>
                        </fo:block>
                    </fo:block-container>
                    <fo:block-container position="absolute" top="75.0pt" left="35.0pt" height="57.0pt" width="400.0pt">
                        <fo:block color="#000000" font-family="OpenSans" font-size="10.0pt">
                            <fo:table table-layout="fixed">
                                <fo:table-column column-width="300.0pt"/>
                                <fo:table-body>
                                    <fo:table-row height="12.0pt">
                                        <fo:table-cell>
                                            <fo:block text-align="start" color="#000000" font-family="OpenSans"
                                                      font-size="18.0pt" font-weight="bold" margin-bottom="25.0pt">
                                                <xsl:apply-templates select="HEADER/SHOPPING_CART"/>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                    <fo:table-row height="12.0pt">
                                        <fo:table-cell>
                                            <fo:block text-align="start" color="#000000" font-family="OpenSans"
                                                      font-size="14.0pt" font-weight="bold">
                                                <xsl:apply-templates select="HEADER/ORDER_STATUS"/>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>
                        </fo:block>
                    </fo:block-container>

                </fo:static-content>

                <fo:flow flow-name="xsl-region-body">
                    <fo:block><!-- GENERATE TABLE START -->
                        <fo:table table-layout="fixed" background-color="transparent" table-omit-footer-at-break="true"
                                  margin-left="5.0pt">
                            <fo:table-column column-width="90.0pt"/>
                            <fo:table-column column-width="140.0pt"/>
                            <fo:table-column column-width="80.0pt"/>
                            <fo:table-column column-width="105.0pt"/>
                            <fo:table-column column-width="93.0pt"/>
                            <fo:table-header>
                                <fo:table-row background-color="#d9d9d9" border-bottom="none" border-color="#aaaaaa"
                                              border-style="solid" border-width="1.0pt">
                                    <fo:table-cell display-align="center">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="10.0pt" start-indent="2.0pt" end-indent="2.0pt"
                                                  padding-top="10.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="12.0pt" font-weight="bold">
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell display-align="center">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="10.0pt" start-indent="2.0pt" end-indent="2.0pt"
                                                  padding-top="10.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="12.0pt" font-weight="bold">
                                            <xsl:apply-templates select="TABLE_HEADER/DESCRIPTION"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell display-align="center">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="10.0pt" start-indent="2.0pt" end-indent="2.0pt"
                                                  padding-top="10.0pt" text-align="center" color="#000000"
                                                  font-family="OpenSans" font-size="12.0pt" font-weight="bold">
                                            <xsl:apply-templates select="TABLE_HEADER/PIECES"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell display-align="center">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="10.0pt" start-indent="2.0pt" end-indent="2.0pt"
                                                  padding-top="10.0pt" text-align="end" color="#000000"
                                                  font-family="OpenSans" font-size="12.0pt" font-weight="bold">
                                            <xsl:apply-templates select="TABLE_HEADER/UNIT_PRICE"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell display-align="center">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="10.0pt" start-indent="2.0pt" end-indent="5.0pt"
                                                  padding-top="10.0pt" text-align="end" color="#000000"
                                                  font-family="OpenSans" font-size="12.0pt" font-weight="bold">
                                            <xsl:apply-templates select="TABLE_HEADER/TOTAL_PRICE"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-header>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell display-align="center">
                                    </fo:table-cell>
                                    <fo:table-cell display-align="center">
                                    </fo:table-cell>
                                    <fo:table-cell display-align="center">
                                    </fo:table-cell>
                                    <fo:table-cell display-align="center">
                                    </fo:table-cell>
                                    <fo:table-cell display-align="center">
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>

                        <xsl:for-each select="CART_ENTRIES/CART_ENTRY">
                            <xsl:variable name="firstItemMargin">
                                <xsl:choose>
                                    <xsl:when test="position() > 0">20.0pt</xsl:when>
                                    <xsl:otherwise>0.0pt</xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>
                            <fo:table table-layout="fixed" background-color="transparent"
                                      table-omit-footer-at-break="true" margin-bottom="{$firstItemMargin}"
                                      margin-left="5.0pt">
                                <fo:table-column column-width="90.0pt"/>
                                <fo:table-column column-width="140.0pt"/>
                                <fo:table-column column-width="80.0pt"/>
                                <fo:table-column column-width="105.0pt"/>
                                <fo:table-column column-width="93.0pt"/>
                                <fo:table-body>
                                    <fo:table-row border-color="#aaaaaa" border-style="solid" border-width="1.0pt"
                                                  height="20.0pt">
                                        <fo:table-cell display-align="before">
                                            <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                      padding-bottom="2.0pt" start-indent="15.0pt" end-indent="2.0pt"
                                                      padding-top="5.0pt" text-align="start" color="#000000"
                                                      font-family="OpenSans" font-size="10.0pt">
                                                <fo:external-graphic content-height="scale-to-fit" height="72.0pt"
                                                                     content-width="130.0pt">
                                                    <xsl:attribute name="src">
                                                        url('<xsl:apply-templates select="PRODUCT_IMAGE_URL"/>')
                                                    </xsl:attribute>
                                                </fo:external-graphic>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell display-align="before">
                                            <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                      padding-bottom="2.0pt" start-indent="2.0pt" end-indent="2.0pt"
                                                      padding-top="5.0pt" text-align="start" color="#000000"
                                                      font-family="OpenSans" font-size="11.0pt" overflow="hidden">
                                                <xsl:apply-templates select="PRODUCT_NAME"/>
                                            </fo:block>
                                            <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                      padding-bottom="2.0pt" start-indent="2.0pt" end-indent="2.0pt"
                                                      padding-top="5.0pt" text-align="start" color="#000000"
                                                      font-family="OpenSans" font-size="9.0pt" overflow="hidden">
                                                <xsl:apply-templates select="ART_NUMBER_LABEL"/>
                                                <xsl:apply-templates select="PRODUCT_CODE"/>
                                            </fo:block>
                                            <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                      padding-bottom="2.0pt" start-indent="2.0pt" end-indent="2.0pt"
                                                      padding-top="2.0pt" text-align="start" color="#000000"
                                                      font-family="OpenSans" font-size="9.0pt" overflow="hidden">
                                                <xsl:apply-templates select="PRODUCT_MANUFACTURER"/>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell display-align="before">
                                            <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                      padding-bottom="2.0pt" start-indent="2.0pt" end-indent="2.0pt"
                                                      padding-top="5.0pt" text-align="center" color="#000000"
                                                      font-family="OpenSans" font-size="10.0pt">
                                                <xsl:apply-templates select="PIECES"/>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell display-align="before">
                                            <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                      padding-bottom="2.0pt" start-indent="2.0pt" end-indent="2.0pt"
                                                      padding-top="5.0pt" text-align="end" color="#000000"
                                                      font-family="OpenSans" font-size="10.0pt">
                                                <xsl:apply-templates select="UNIT_PRICE"/>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell display-align="before">
                                            <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                      padding-bottom="2.0pt" start-indent="2.0pt" end-indent="5.0pt"
                                                      padding-top="5.0pt" text-align="end" color="#000000"
                                                      font-family="OpenSans" font-size="10.0pt">
                                                <xsl:apply-templates select="TOTAL_PRICE"/>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>
                        </xsl:for-each>

                        <fo:table table-layout="fixed" background-color="transparent" table-omit-footer-at-break="true"
                                  margin-bottom="20.0pt" margin-left="255.0pt">
                            <fo:table-column column-width="130.0pt"/>
                            <fo:table-column column-width="130.0pt"/>
                            <fo:table-body>
                                <fo:table-row border-color="#aaaaaa" border-style="solid" border-bottom="none"
                                              border-width="1.0pt" height="20.0pt">
                                    <fo:table-cell display-align="center">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="2.0pt" start-indent="10.0pt" end-indent="2.0pt"
                                                  padding-top="5.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="10.0pt">
                                            <xsl:apply-templates select="CART_ENTRIES/SUBTOTAL_LABEL"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="2.0pt" start-indent="10.0pt" end-indent="2.0pt"
                                                  padding-top="5.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="10.0pt">
                                            <xsl:apply-templates select="CART_ENTRIES/SUBTOTAL"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row border-color="#aaaaaa" border-style="solid" border-bottom="none"
                                              border-top="none" border-width="1.0pt" height="20.0pt">
                                    <fo:table-cell display-align="center">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="2.0pt" start-indent="10.0pt" end-indent="2.0pt"
                                                  padding-top="5.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="10.0pt">
                                            <xsl:apply-templates select="CART_ENTRIES/DELIVERY_COST_LABEL"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="2.0pt" start-indent="10.0pt" end-indent="2.0pt"
                                                  padding-top="5.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="10.0pt">
                                            <xsl:apply-templates select="CART_ENTRIES/DELIVERY_COST"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row border-color="#aaaaaa" border-style="solid" border-bottom="none"
                                              border-top="none" border-width="1.0pt" height="20.0pt">
                                    <fo:table-cell display-align="center">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="10.0pt" start-indent="10.0pt" end-indent="2.0pt"
                                                  padding-top="5.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="10.0pt">
                                            <xsl:apply-templates select="CART_ENTRIES/TOTAL_TAX_LABEL"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="10.0pt" start-indent="10.0pt" end-indent="2.0pt"
                                                  padding-top="5.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="10.0pt">
                                            <xsl:apply-templates select="CART_ENTRIES/TOTAL_TAX"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row border-color="#aaaaaa" background-color="#d9d9d9" border-style="solid"
                                              border-width="1.0pt" height="20.0pt">
                                    <fo:table-cell border-color="#aaaaaa" border-left-style="solid"
                                                   border-width="1.0pt">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  start-indent="10.0pt" end-indent="5.0pt" text-align="left"
                                                  color="#000000" font-family="OpenSans" font-size="12.0pt"
                                                  padding-top="5.0pt" padding-bottom="5.0pt" font-weight="bold">
                                            <xsl:apply-templates select="CART_ENTRIES/TOTAL_PRICE_LABEL"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  start-indent="10.0pt" end-indent="5.0pt" text-align="left"
                                                  color="#000000" font-family="OpenSans" font-size="12.0pt"
                                                  padding-top="5.0pt" padding-bottom="5.0pt" font-weight="bold">
                                            <xsl:apply-templates select="CART_ENTRIES/TOTAL_PRICE"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>

                        <fo:table table-layout="fixed" background-color="transparent" table-omit-footer-at-break="true"
                                  margin-bottom="20.0pt" margin-left="5.0pt">
                            <fo:table-column column-width="170.0pt"/>
                            <fo:table-column column-width="170.0pt"/>
                            <fo:table-column column-width="170.0pt"/>
                            <fo:table-header>
                                <fo:table-row border-color="#aaaaaa" border-style="solid" border-width="1.0pt"
                                              height="20.0pt" background-color="#d9d9d9">
                                    <fo:table-cell display-align="before">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="2.0pt" start-indent="10.0pt" end-indent="2.0pt"
                                                  padding-top="5.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="10.0pt">
                                            <xsl:apply-templates select="SIGNATURES/HEADERS/NAME"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell display-align="before">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="2.0pt" start-indent="10.0pt" end-indent="2.0pt"
                                                  padding-top="5.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="10.0pt">
                                            <xsl:apply-templates select="SIGNATURES/HEADERS/SIGNATURE"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell display-align="before">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="2.0pt" start-indent="10.0pt" end-indent="2.0pt"
                                                  padding-top="5.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="10.0pt">
                                            <xsl:apply-templates select="SIGNATURES/HEADERS/DATE"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-header>
                            <fo:table-body>
                                <fo:table-row border-color="#aaaaaa" border-style="solid" border-bottom="none"
                                              border-width="1.0pt" height="20.0pt">
                                    <fo:table-cell display-align="center">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="2.0pt" start-indent="10.0pt" end-indent="2.0pt"
                                                  padding-top="5.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="10.0pt">
                                            <xsl:text></xsl:text>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell display-align="before">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="2.0pt" start-indent="2.0pt" end-indent="2.0pt"
                                                  padding-top="5.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="10.0pt">
                                            <xsl:text></xsl:text>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell display-align="before">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="2.0pt" start-indent="2.0pt" end-indent="2.0pt"
                                                  padding-top="5.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="10.0pt">
                                            <xsl:text></xsl:text>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row border-color="#aaaaaa" border-style="solid" border-bottom="solid"
                                              border-top="none" border-width="1.0pt" height="20.0pt">
                                    <fo:table-cell display-align="before">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="2.0pt" start-indent="10.0pt" end-indent="2.0pt"
                                                  padding-top="5.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="10.0pt">
                                            <xsl:text>________________________________</xsl:text>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell display-align="before">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="2.0pt" start-indent="10.0pt" end-indent="2.0pt"
                                                  padding-top="5.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="10.0pt">
                                            <xsl:text>________________________________</xsl:text>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell display-align="before">
                                        <fo:block white-space-collapse="false" linefeed-treatment="preserve"
                                                  padding-bottom="2.0pt" start-indent="10.0pt" end-indent="2.0pt"
                                                  padding-top="5.0pt" text-align="start" color="#000000"
                                                  font-family="OpenSans" font-size="10.0pt">
                                            <xsl:text>________________________________</xsl:text>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>

                        <fo:block-container height="57.0pt" width="500.0pt">
                            <fo:block color="#000000" font-family="OpenSans" font-size="10.0pt">
                                <fo:table table-layout="fixed" border-color="#aaaaaa" border-style="solid"
                                          border-width="1.0pt" table-omit-footer-at-break="true" margin-left="5.0pt">
                                    <fo:table-column column-width="509.0pt"/>
                                    <fo:table-body>
                                        <fo:table-row height="12.0pt" padding-top="10.0pt" padding-bottom="10.0pt">
                                            <fo:table-cell>
                                                <fo:block start-indent="10.0pt" end-indent="2.0pt" padding-top="10.0pt">
                                                    <xsl:apply-templates select="HEADER/COMPANY_NAME"/>
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                        <fo:table-row height="12.0pt">
                                            <fo:table-cell>
                                                <fo:block start-indent="10.0pt" end-indent="2.0pt">
                                                    <xsl:apply-templates select="HEADER/STREET"/>
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                        <fo:table-row height="12.0pt">
                                            <fo:table-cell>
                                                <fo:block start-indent="10.0pt" end-indent="2.0pt">
                                                    <xsl:apply-templates select="HEADER/CITY"/>
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                        <xsl:if test="normalize-space(CURRENT_SITE) != 'distrelec_FR'">
                                            <fo:table-row height="12.0pt">
                                                <fo:table-cell start-indent="10.0pt" end-indent="2.0pt">
                                                    <fo:block>
                                                        <xsl:apply-templates select="HEADER/TEL"/>
                                                    </fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </xsl:if>
                                        <fo:table-row height="12.0pt">
                                            <fo:table-cell>
                                                <fo:block start-indent="10.0pt" end-indent="2.0pt">
                                                    <xsl:apply-templates select="HEADER/URL"/>
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                        <fo:table-row height="12.0pt">
                                            <fo:table-cell>
                                                <fo:block start-indent="10.0pt" end-indent="2.0pt">
                                                    <xsl:apply-templates select="HEADER/EMAIL"/>
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                        <fo:table-row height="12.0pt">
                                            <fo:table-cell start-indent="10.0pt" end-indent="2.0pt">
                                                <fo:block>
                                                    <xsl:apply-templates select="HEADER/VAT"/>
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>
                            </fo:block>
                        </fo:block-container>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
