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

<xsl:stylesheet
        xmlns:fox="http://xml.apache.org/fop/extensions"
        xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.1"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:saxon="http://icl.com/saxon" extension-element-prefixes="saxon">

    <xsl:template match="PRODUCT-NO-SVHC-RND">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master
                        master-name="masterNamePageMain0" page-height="845.0pt"
                        page-width="598.0pt" margin-top="20.0pt" margin-left="60.0pt"
                        margin-bottom="40pt" margin-right="40pt">
                    <fo:region-body margin-left="28.0pt"
                                    margin-top="94.0pt" margin-bottom="40.0pt" margin-right="28.0pt"/>
                    <fo:region-before extent="94.0pt"
                                      precedence="true"/>
                    <fo:region-after extent="87.0pt" precedence="true"/>
                    <fo:region-start extent="28.0pt" precedence="false"/>
                    <fo:region-end extent="28.0pt" precedence="false"/>
                </fo:simple-page-master>

                <fo:simple-page-master
                        master-name="masterNamePageFirst1" page-height="845.0pt"
                        page-width="598.0pt" margin-top="20.0pt" margin-left="60.0pt"
                        margin-bottom="40pt" margin-right="40pt">
                    <fo:region-body margin-left="28.0pt"
                                    margin-top="165.0pt" margin-bottom="40.0pt" margin-right="28.0pt"/>
                    <fo:region-before region-name="regionBefore1"
                                      extent="770.0pt" precedence="true"/>
                    <fo:region-after region-name="regionAfter1"
                                     extent="99.0pt" precedence="true"/>
                    <fo:region-start region-name="regionStart1"
                                     extent="28.0pt" precedence="false"/>
                    <fo:region-end region-name="regionEnd1"
                                   extent="28.0pt" precedence="false"/>
                </fo:simple-page-master>

                <fo:page-sequence-master
                        master-name="masterSequenceName1">
                    <fo:repeatable-page-master-alternatives>
                        <fo:conditional-page-master-reference
                                master-reference="masterNamePageFirst1" page-position="first"/>
                    </fo:repeatable-page-master-alternatives>
                </fo:page-sequence-master>
            </fo:layout-master-set>

            <fo:page-sequence master-name="masterNamePageMain0"
                              master-reference="masterSequenceName1">
                <fo:static-content flow-name="regionBefore1">
                    <fo:block-container font-family="OpenSans">
                        <fo:block>
                            <fo:table table-layout="fixed">
                                <fo:table-column column-width="66%"/>
                                <fo:table-column column-width="34%"/>

                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell>
                                            <fo:block>
                                                <xsl:text>&#160;</xsl:text>
                                            </fo:block>
                                            <fo:block color="#808080" font-size="6">
                                                <xsl:apply-templates select="HEADER"/>
                                            </fo:block>

                                            <fo:block color="#808080" margin-top="3pt" font-size="22" font-weight="bold">
                                                <xsl:apply-templates select="HEADING1"/>
                                            </fo:block>
                                        </fo:table-cell>

                                        <fo:table-cell>
                                            <fo:block text-align="right">
                                                <fo:external-graphic content-width="140pt">
                                                    <xsl:attribute name="src">url('<xsl:apply-templates select="RND_LOGO_URL"/>') /></xsl:attribute>
                                                </fo:external-graphic>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>
                        </fo:block>

                        <fo:block color="#808080" font-size="13" font-weight="bold">
                            <xsl:apply-templates select="HEADING2"/>
                        </fo:block>
                    </fo:block-container>

                    <fo:block-container font-family="OpenSans" font-size="11pt" line-height="1.3" margin-top="25pt">
                        <fo:block margin-bottom="10pt">
                            <fo:inline>
                                <xsl:apply-templates select="PARAGRAPH1BEGIN"/>
                            </fo:inline>
                            <xsl:text>&#160;</xsl:text>
                            <fo:inline font-weight="bold">
                                <xsl:apply-templates select="PARAGRAPH1MID"/>
                            </fo:inline>
                            <xsl:text>&#160;</xsl:text>
                            <fo:inline>
                                <xsl:apply-templates select="PARAGRAPH1END"/>
                            </fo:inline>
                        </fo:block>

                        <fo:block margin-bottom="20pt">
                            <xsl:apply-templates select="PARAGRAPH2"/>
                        </fo:block>

                        <fo:block margin-bottom="10pt" font-size="13pt" font-weight="bold">
                            <xsl:apply-templates
                                    select="PRODUCTHEADER"/>
                        </fo:block>

                        <fo:block margin-bottom="30pt">
                            <fo:block margin-bottom="10pt">
                                <xsl:apply-templates select="RND_ARTICLENUMBER_LABEL"/>
                                <xsl:text>&#160;</xsl:text>
                                <xsl:apply-templates select="RND_ARTICLENUMBER"/>
                            </fo:block>

                            <fo:block margin-bottom="10pt">
                                <xsl:apply-templates select="DISTRELEC_ARTICLENUMBER_LABEL"/>
                                <xsl:text>&#160;</xsl:text>
                                <xsl:apply-templates select="DISTRELEC_ARTICLENUMBER"/>
                            </fo:block>

                            <fo:block margin-bottom="10pt">
                                <xsl:apply-templates select="DESCRIPTION_LABEL"/>
                                <xsl:text>&#160;</xsl:text>
                                <xsl:apply-templates select="DESCRIPTION"/>
                            </fo:block>

                            <fo:block margin-bottom="15pt">
                                <xsl:apply-templates select="DATE_LABEL"/>
                                <xsl:text>&#160;</xsl:text>
                                <xsl:apply-templates select="DATE_VALUE"/>
                            </fo:block>
                        </fo:block>

                        <fo:block margin-bottom="10pt" font-size="13pt" font-weight="bold">
                            <xsl:apply-templates
                                    select="PRODUCTHEADER2"/>
                        </fo:block>

                        <fo:block>
                            <xsl:apply-templates select="PARAGRAPH3"/>
                        </fo:block>

                        <fo:block>
                            <fo:basic-link color="blue" external-destination="url('https://echa.europa.eu/candidate-list-table')">
                                https://echa.europa.eu/candidate-list-table
                            </fo:basic-link>
                        </fo:block>
                    </fo:block-container>

                    <fo:block-container font-family="OpenSans" font-size="11pt">
                        <fo:block margin-top="100pt" margin-right="5%">
                            <fo:block text-align="right" margin-right="15pt">
                                <fo:external-graphic content-width="200pt">
                                    <xsl:attribute name="src">url('<xsl:apply-templates select="DISTRELEC_LOGO_URL"/>')</xsl:attribute>
                                </fo:external-graphic>
                            </fo:block>

                            <fo:leader leader-pattern="rule" leader-length="100%" rule-style="solid" rule-thickness="1pt"/>

                            <fo:block margin-top="20pt">
                                <xsl:apply-templates select="VALID_DOCUMENT"/>
                            </fo:block>
                        </fo:block>
                    </fo:block-container>

                    <fo:block-container font-family="OpenSans" position="absolute" bottom="0" height="11pt" font-size="11pt">
                        <fo:block color="#808080">
                            <fo:table table-layout="fixed">
                                <fo:table-column/>
                                <fo:table-column/>
                                <fo:table-column/>

                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell />

                                        <fo:table-cell text-align="center">
                                            <fo:block font-weight="bold">
                                                <xsl:apply-templates select="FOOTER_PAGE"/>
                                            </fo:block>
                                        </fo:table-cell>

                                        <fo:table-cell text-align="right">
                                            <fo:block>
                                                <xsl:apply-templates select="FOOTER_EMAIL"/>
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
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
