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
        xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.1"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:saxon="http://icl.com/saxon" extension-element-prefixes="saxon">

    <xsl:template match="BATTERY-COMPLIANCE">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

            <fo:layout-master-set>
                <fo:simple-page-master
                        master-name="masterPage"
                        page-height="845.0pt"
                        page-width="598.0pt"
                        margin-top="0.0pt"
                        margin-left="40.0pt"
                        margin-bottom="40.0pt"
                        margin-right="40.0pt">
                    <fo:region-body margin-left="28.0pt"
                                    margin-top="120.0pt"
                                    margin-bottom="40.0pt"
                                    margin-right="28.0pt" />
                    <fo:region-before region-name="headerRegion"
                                      extent="100.0pt"
                                      precedence="true" />
                    <fo:region-after region-name="footerRegion"
                                     extent="40.0pt"
                                     precedence="true" />
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="masterPage">

                <!-- HEADER -->
                <fo:static-content flow-name="headerRegion">
                    <fo:block color="#000000" font-family="OpenSans"
                              font-size="13.0pt">
                        <fo:table table-layout="fixed">
                            <fo:table-column column-width="500.0pt" />
                            <fo:table-body>
                                <fo:table-row height="12.0pt" font-size="18.0pt">
                                    <fo:table-cell>
                                        <fo:table table-layout="fixed">
                                            <fo:table-column column-width="100.0pt" />
                                            <fo:table-column column-width="200.0pt" />
                                            <fo:table-body>
                                                <fo:table-row>
                                                    <fo:table-cell text-align="left">
                                                        <fo:block-container
                                                                position="absolute"  left="-40.0pt"
                                                                height="57.0pt" width="328.0pt">

                                                            <fo:block>
                                                                <fo:external-graphic>
                                                                    <xsl:attribute name="src">url('<xsl:apply-templates
                                                                            select="COLOUR_BRANDING" />')</xsl:attribute>
                                                                </fo:external-graphic>
                                                            </fo:block>
                                                        </fo:block-container>

                                                    </fo:table-cell>
                                                    <fo:table-cell text-align="right">
                                                        <fo:block-container height="57.0pt" width="425.0pt">
                                                            <fo:block-container>
                                                                <fo:block>
                                                                    <fo:external-graphic
                                                                            content-height="auto" height="50.0pt"
                                                                            content-width="130.0pt">
                                                                        <xsl:attribute name="src">url('<xsl:apply-templates
                                                                                select="LOGO_URL" />')</xsl:attribute>
                                                                    </fo:external-graphic>
                                                                </fo:block>
                                                            </fo:block-container>
                                                        </fo:block-container>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                            </fo:table-body>
                                        </fo:table>
                                    </fo:table-cell>
                                </fo:table-row>

                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                </fo:static-content>

                <!-- FOOTER -->
                <fo:static-content flow-name="footerRegion">
                    <fo:block font-family="OpenSans">
                        <fo:block text-align="center" color="red">
                            <fo:leader leader-pattern="rule" leader-length="100%" rule-style="solid" rule-thickness="0.3pt"/>
                        </fo:block>
                        <fo:block text-align="center" color="#000" font-size="10.0pt" margin-top="2.0pt">
                            <xsl:apply-templates select="FOOTER_ADDRESS" />
                        </fo:block>
                    </fo:block>
                </fo:static-content>

                <!-- BODY -->
                <fo:flow flow-name="xsl-region-body">
                    <fo:block color="#000000" font-family="OpenSans"
                              font-size="10.0pt" break-after="auto">
                        <fo:table table-layout="fixed">
                            <fo:table-column column-width="470.0pt"/>
                            <fo:table-body>
                                <fo:table-row height="12.0pt" font-size="16.0pt">
                                    <fo:table-cell text-align="center">
                                        <fo:block font-weight="bold">
                                            <xsl:apply-templates select="TITLE"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="30.0pt">
                                    <fo:table-cell>
                                        <fo:block>

                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="10.0pt">
                                    <fo:table-cell>
                                        <fo:block>

                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="12.0pt">
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:apply-templates select="PARAGRAPH1"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="10.0pt">
                                    <fo:table-cell>
                                        <fo:block>

                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="12.0pt">
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:apply-templates select="PARAGRAPH2"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="10.0pt">
                                    <fo:table-cell>
                                        <fo:block>

                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="12.0pt">
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:apply-templates select="PARAGRAPH3"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="10.0pt">
                                    <fo:table-cell>
                                        <fo:block>

                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="12.0pt">
                                    <fo:table-cell>
                                        <fo:block text-align="left">
                                            <fo:external-graphic content-width="90pt" content-height="120">
                                                <xsl:attribute name="src">url('<xsl:apply-templates select="RECYCLE_BIN_URL"/>') />
                                                </xsl:attribute>
                                            </fo:external-graphic>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="10.0pt">
                                    <fo:table-cell>
                                        <fo:block>

                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="12.0pt">
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:apply-templates select="PARAGRAPH4LINE1"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="12.0pt">
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:apply-templates select="PARAGRAPH4LINE2"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="12.0pt">
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:apply-templates select="PARAGRAPH4LINE3"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="10.0pt">
                                    <fo:table-cell>
                                        <fo:block>

                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="12.0pt">
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:apply-templates select="PARAGRAPH5"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="10.0pt">
                                    <fo:table-cell>
                                        <fo:block>

                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="12.0pt">
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:apply-templates select="PARAGRAPH6"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="10.0pt">
                                    <fo:table-cell>
                                        <fo:block>

                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row height="12.0pt">
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:apply-templates select="PARAGRAPH7LINE1"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="12.0pt">
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:apply-templates select="PARAGRAPH7LINE2"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="12.0pt">
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:apply-templates select="PARAGRAPH7LINE3"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                </fo:flow>

            </fo:page-sequence>

        </fo:root>
    </xsl:template>
</xsl:stylesheet>
