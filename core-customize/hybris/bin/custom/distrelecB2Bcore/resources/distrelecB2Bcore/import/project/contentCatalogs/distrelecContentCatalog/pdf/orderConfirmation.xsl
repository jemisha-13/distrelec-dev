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

<xsl:stylesheet xmlns:fox="http://xml.apache.org/fop/extensions" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.1"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:saxon="http://icl.com/saxon" extension-element-prefixes="saxon">

	<xsl:template match="ORDER">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="masterNamePageMain0" page-height="845.0pt" page-width="598.0pt" margin-top="10.0pt" margin-left="10.0pt"
					margin-bottom="10.0pt" margin-right="10.0pt">
					<fo:region-body margin-left="28.0pt" margin-top="94.0pt" margin-bottom="40.0pt" margin-right="28.0pt" />
					<fo:region-before extent="94.0pt" precedence="true" />
					<fo:region-after extent="87.0pt" precedence="true" />
					<fo:region-start extent="28.0pt" precedence="false" />
					<fo:region-end extent="28.0pt" precedence="false" />
				</fo:simple-page-master>
				<fo:simple-page-master master-name="masterNamePageFirst1" page-height="845.0pt" page-width="598.0pt" margin-top="10.0pt" margin-left="10.0pt"
					margin-bottom="10.0pt" margin-right="10.0pt">
					<fo:region-body margin-left="28.0pt" margin-top="365.0pt" margin-bottom="40.0pt" margin-right="28.0pt" />
					<fo:region-before region-name="regionBefore1" extent="493.0pt" precedence="true" />
					<fo:region-after region-name="regionAfter1" extent="99.0pt" precedence="true" />
					<fo:region-start region-name="regionStart1" extent="28.0pt" precedence="false" />
					<fo:region-end region-name="regionEnd1" extent="28.0pt" precedence="false" />
				</fo:simple-page-master>
				<fo:simple-page-master master-name="masterNamePageRest2" page-height="845.0pt" page-width="598.0pt" margin-top="10.0pt" margin-left="10.0pt"
					margin-bottom="10.0pt" margin-right="10.0pt">
					<fo:region-body margin-left="28.0pt" margin-top="40.0pt" margin-bottom="40.0pt" margin-right="28.0pt" />
					<fo:region-before region-name="regionBefore2" extent="126.0pt" precedence="true" />
					<fo:region-after region-name="regionAfter2" extent="99.0pt" precedence="true" />
					<fo:region-start region-name="regionStart2" extent="28.0pt" precedence="false" />
					<fo:region-end region-name="regionEnd2" extent="28.0pt" precedence="false" />
				</fo:simple-page-master>
				<fo:page-sequence-master master-name="masterSequenceName1">
					<fo:repeatable-page-master-alternatives>
						<fo:conditional-page-master-reference master-reference="masterNamePageFirst1" page-position="first" />
						<fo:conditional-page-master-reference master-reference="masterNamePageRest2" page-position="rest" />
					</fo:repeatable-page-master-alternatives>
				</fo:page-sequence-master>
			</fo:layout-master-set>
			<fo:page-sequence master-name="masterNamePageMain0" master-reference="masterSequenceName1">
				<fo:static-content flow-name="regionBefore1">
					<fo:block-container position="absolute" top="29.0pt" left="420.0pt" height="72.0pt" width="130.0pt">
						<fo:block>
							<fo:external-graphic inline-progression-dimension.maximum="100%" content-width="scale-down-to-fit">
								<xsl:attribute name="src">url('<xsl:apply-templates select="HEADER/LOGO_URL" />')</xsl:attribute>
							</fo:external-graphic>
						</fo:block>
					</fo:block-container>

					<fo:block-container position="absolute" top="29.0pt" left="30.0pt" height="57.0pt" width="328.0pt">
						<fo:block color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
							<fo:table table-layout="fixed">
								<fo:table-column column-width="130.0pt" />
								<fo:table-body>
									<fo:table-row height="12.0pt">
										<fo:table-cell>
											<fo:block>
												<xsl:apply-templates select="HEADER/COMPANY_NAME" />
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row height="12.0pt">
										<fo:table-cell>
											<fo:block>
												<xsl:apply-templates select="HEADER/STREET" />
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row height="12.0pt">
										<fo:table-cell>
											<fo:block>
												<xsl:apply-templates select="HEADER/CITY" />
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row height="12.0pt">
										<fo:table-cell>
											<fo:block>
												<xsl:apply-templates select="HEADER/TEL" />
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row height="12.0pt">
										<fo:table-cell>
											<fo:block>
												<xsl:apply-templates select="HEADER/FAX" />
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row height="12.0pt">
										<fo:table-cell>
											<fo:block>
												<xsl:apply-templates select="HEADER/URL" />
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row height="12.0pt">
										<fo:table-cell>
											<fo:block>
												<xsl:apply-templates select="HEADER/EMAIL" />
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row height="12.0pt">
										<fo:table-cell>
											<fo:block>
												<xsl:apply-templates select="HEADER/VAT" />
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
					</fo:block-container>

					<fo:block-container position="absolute" top="145.0pt" left="30.0pt" width="260.0pt">
						<fo:block span="none" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
							<fo:table table-layout="fixed">
								<fo:table-column column-width="260.0pt" />
								<fo:table-body>
									<fo:table-row height="16.0pt">
										<fo:table-cell>
											<fo:block font-weight="bold">
												<xsl:apply-templates select="PAYMENT_ADDRESS/LABEL" />
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<xsl:choose>
										<xsl:when test="(ORDER_INFO/USER_TYPE = 'B2C') or (ORDER_INFO/USER_TYPE = 'GUEST') or (ORDER_INFO/USER_TYPE = 'B2E')">
											<fo:table-row height="12.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="PAYMENT_ADDRESS/NAME" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<xsl:if test="PAYMENT_ADDRESS/ADDITIONAL_ADDRESS">
												<fo:table-row height="12.0pt">
													<fo:table-cell>
														<fo:block>
															<xsl:apply-templates select="PAYMENT_ADDRESS/ADDITIONAL_ADDRESS" />
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</xsl:if>
											<fo:table-row height="12.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="PAYMENT_ADDRESS/STREET" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<xsl:if test="PAYMENT_ADDRESS/PO_BOX">
												<fo:table-row height="12.0pt">
													<fo:table-cell>
														<fo:block>
															<xsl:apply-templates select="PAYMENT_ADDRESS/PO_BOX" />
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</xsl:if>
											<fo:table-row height="12.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="PAYMENT_ADDRESS/CITY" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row height="12.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="PAYMENT_ADDRESS/COUNTRY" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row height="12.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="PAYMENT_ADDRESS/EMAIL" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</xsl:when>
										<xsl:otherwise>
											<fo:table-row height="12.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="PAYMENT_ADDRESS/COMPANY_NAME" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<xsl:if test="PAYMENT_ADDRESS/COMPANY_NAME_2">
												<fo:table-row height="12.0pt">
													<fo:table-cell>
														<fo:block>
															<xsl:apply-templates select="PAYMENT_ADDRESS/COMPANY_NAME_2" />
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</xsl:if>
											<xsl:if test="PAYMENT_ADDRESS/ADDITIONAL_ADDRESS">
												<fo:table-row height="12.0pt">
													<fo:table-cell>
														<fo:block>
															<xsl:apply-templates select="PAYMENT_ADDRESS/ADDITIONAL_ADDRESS" />
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</xsl:if>
											<fo:table-row height="12.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="PAYMENT_ADDRESS/STREET" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<xsl:if test="PAYMENT_ADDRESS/PO_BOX">
												<fo:table-row height="12.0pt">
													<fo:table-cell>
														<fo:block>
															<xsl:apply-templates select="PAYMENT_ADDRESS/PO_BOX" />
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</xsl:if>
											<fo:table-row height="12.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="PAYMENT_ADDRESS/CITY" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row height="12.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="PAYMENT_ADDRESS/COUNTRY" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row height="12.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="PAYMENT_ADDRESS/EMAIL" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</xsl:otherwise>
									</xsl:choose>
								</fo:table-body>
							</fo:table>
						</fo:block>
					</fo:block-container>

					<xsl:choose>
						<xsl:when test="ORDER_INFO/DELIVERY_MODE_CODE = 'Movex_PickUp'">
							<fo:block-container position="absolute" top="145.0pt" left="290.0pt" width="260.0pt">
								<fo:block span="none" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
									<fo:table table-layout="fixed">
										<fo:table-column column-width="260.0pt" />
										<fo:table-body>
											<fo:table-row height="16.0pt">
												<fo:table-cell>
													<fo:block font-weight="bold">
														<xsl:apply-templates select="PICKUP_LOCATION/LABEL" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row height="12.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="PICKUP_LOCATION/NAME" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row height="12.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="PICKUP_LOCATION/STREET" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row height="12.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="PICKUP_LOCATION/CITY" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row height="12.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="PICKUP_LOCATION/PHONE" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row height="12.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="PICKUP_LOCATION/OPENING_HOURS_MO_FR" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row height="12.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="PICKUP_LOCATION/OPENING_HOURS_SA" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
								</fo:block>
							</fo:block-container>
						</xsl:when>
						<xsl:otherwise>
							<fo:block-container position="absolute" top="145.0pt" left="290.0pt" width="260.0pt">
								<fo:block span="none" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
									<fo:table table-layout="fixed">
										<fo:table-column column-width="260.0pt" />
										<fo:table-body>
											<fo:table-row height="16.0pt">
												<fo:table-cell>
													<fo:block font-weight="bold">
														<xsl:apply-templates select="DELIVERY_ADDRESS/LABEL" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<xsl:choose>
												<xsl:when test="(ORDER_INFO/USER_TYPE = 'B2C') or (ORDER_INFO/USER_TYPE = 'GUEST') or (ORDER_INFO/USER_TYPE = 'B2E')">
													<fo:table-row height="12.0pt">
														<fo:table-cell>
															<fo:block>
																<xsl:apply-templates select="DELIVERY_ADDRESS/NAME" />
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<xsl:if test="DELIVERY_ADDRESS/ADDITIONAL_ADDRESS">
														<fo:table-row height="12.0pt">
															<fo:table-cell>
																<fo:block>
																	<xsl:apply-templates select="DELIVERY_ADDRESS/ADDITIONAL_ADDRESS" />
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</xsl:if>
													<fo:table-row height="12.0pt">
														<fo:table-cell>
															<fo:block>
																<xsl:apply-templates select="DELIVERY_ADDRESS/STREET" />
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<xsl:if test="DELIVERY_ADDRESS/PO_BOX">
														<fo:table-row height="12.0pt">
															<fo:table-cell>
																<fo:block>
																	<xsl:apply-templates select="DELIVERY_ADDRESS/PO_BOX" />
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</xsl:if>
													<fo:table-row height="12.0pt">
														<fo:table-cell>
															<fo:block>
																<xsl:apply-templates select="DELIVERY_ADDRESS/CITY" />
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row height="12.0pt">
														<fo:table-cell>
															<fo:block>
																<xsl:apply-templates select="DELIVERY_ADDRESS/COUNTRY" />
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row height="12.0pt">
														<fo:table-cell>
															<fo:block>
																<xsl:apply-templates select="DELIVERY_ADDRESS/EMAIL" />
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:when>
												<xsl:otherwise>
													<fo:table-row height="12.0pt">
														<fo:table-cell>
															<fo:block>
																<xsl:apply-templates select="DELIVERY_ADDRESS/COMPANY_NAME" />
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<xsl:if test="DELIVERY_ADDRESS/COMPANY_NAME_2">
														<fo:table-row height="12.0pt">
															<fo:table-cell>
																<fo:block>
																	<xsl:apply-templates select="DELIVERY_ADDRESS/COMPANY_NAME_2" />
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</xsl:if>
													<xsl:if test="DELIVERY_ADDRESS/ADDITIONAL_ADDRESS">
														<fo:table-row height="12.0pt">
															<fo:table-cell>
																<fo:block>
																	<xsl:apply-templates select="DELIVERY_ADDRESS/ADDITIONAL_ADDRESS" />
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</xsl:if>
													<fo:table-row height="12.0pt">
														<fo:table-cell>
															<fo:block>
																<xsl:apply-templates select="DELIVERY_ADDRESS/NAME" />
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row height="12.0pt">
														<fo:table-cell>
															<fo:block>
																<xsl:apply-templates select="DELIVERY_ADDRESS/STREET" />
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<xsl:if test="DELIVERY_ADDRESS/PO_BOX">
														<fo:table-row height="12.0pt">
															<fo:table-cell>
																<fo:block>
																	<xsl:apply-templates select="DELIVERY_ADDRESS/PO_BOX" />
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</xsl:if>
													<fo:table-row height="12.0pt">
														<fo:table-cell>
															<fo:block>
																<xsl:apply-templates select="DELIVERY_ADDRESS/CITY" />
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row height="12.0pt">
														<fo:table-cell>
															<fo:block>
																<xsl:apply-templates select="DELIVERY_ADDRESS/COUNTRY" />
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row height="12.0pt">
														<fo:table-cell>
															<fo:block>
																<xsl:apply-templates select="DELIVERY_ADDRESS/EMAIL" />
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:otherwise>
											</xsl:choose>
										</fo:table-body>
									</fo:table>
								</fo:block>
							</fo:block-container>
						</xsl:otherwise>
					</xsl:choose>

					<fo:block-container position="absolute" top="270.0pt" left="30.0pt" width="520.0pt">
						<fo:block span="none" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
							<fo:table table-layout="fixed">
								<fo:table-column column-width="130.0pt" />
								<fo:table-column column-width="130.0pt" />
								<fo:table-column column-width="130.0pt" />
								<fo:table-column column-width="130.0pt" />
								<fo:table-body>
									<fo:table-row height="16.0pt">
										<fo:table-cell>
											<fo:block>
												<xsl:apply-templates select="ORDER_INFO/ORDER_CODE_LABEL" />
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>
												<xsl:apply-templates select="ORDER_INFO/ORDER_CODE" />
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>
												<xsl:apply-templates select="ORDER_INFO/USER_NUMBER_LABEL" />
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>
												<xsl:apply-templates select="ORDER_INFO/USER_NUMBER" />
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<xsl:choose>
										<xsl:when test="(ORDER_INFO/COST_CENTER_NUMBER) and (ORDER_INFO/PROJECT_NUMBER)">
											<fo:table-row height="16.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/COST_CENTER_NUMBER_LABEL" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/COST_CENTER_NUMBER" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/DATE_LABEL" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/DATE" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row height="16.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/PROJECT_NUMBER_LABEL" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/PROJECT_NUMBER" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/DELIVERY_MODE_LABEL" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/DELIVERY_MODE" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row height="16.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/PAYMENT_MODE_LABEL" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/PAYMENT_MODE" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell></fo:table-cell>
												<fo:table-cell></fo:table-cell>
											</fo:table-row>
										</xsl:when>
										<xsl:when test="ORDER_INFO/COST_CENTER_NUMBER">
											<fo:table-row height="16.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/COST_CENTER_NUMBER_LABEL" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/COST_CENTER_NUMBER" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/DATE_LABEL" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/DATE" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row height="16.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/PAYMENT_MODE_LABEL" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/PAYMENT_MODE" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/DELIVERY_MODE_LABEL" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/DELIVERY_MODE" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</xsl:when>
										<xsl:when test="ORDER_INFO/PROJECT_NUMBER">
											<fo:table-row height="16.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/PROJECT_NUMBER_LABEL" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/PROJECT_NUMBER" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/DATE_LABEL" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/DATE" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row height="16.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/PAYMENT_MODE_LABEL" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/PAYMENT_MODE" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/DELIVERY_MODE_LABEL" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/DELIVERY_MODE" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</xsl:when>
										<xsl:otherwise>
											<fo:table-row height="16.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/DATE_LABEL" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/DATE" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell></fo:table-cell>
												<fo:table-cell></fo:table-cell>
											</fo:table-row>
											<fo:table-row height="16.0pt">
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/PAYMENT_MODE_LABEL" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/PAYMENT_MODE" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/DELIVERY_MODE_LABEL" />
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<xsl:apply-templates select="ORDER_INFO/DELIVERY_MODE" />
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</xsl:otherwise>
									</xsl:choose>
									
								</fo:table-body>
							</fo:table>
						</fo:block>
					</fo:block-container>
				</fo:static-content>
				
				<fo:flow flow-name="xsl-region-body">
					<fo:block><!-- GENERATE TABLE START -->
						<fo:table table-layout="fixed" background-color="transparent" table-omit-footer-at-break="true" margin-bottom="20.0pt">
							<fo:table-column column-width="50.0pt" />
							<fo:table-column column-width="130.0pt" />
							<fo:table-column column-width="130.0pt" />
							<fo:table-column column-width="50.0pt" />
							<fo:table-column column-width="85.0pt" />
							<fo:table-column column-width="85.0pt" />
							<fo:table-header>
								<fo:table-row border-color="#aaaaaa" border-style="solid" border-width="1.0pt">
									<fo:table-cell display-align="center" background-color="#ffffff">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" padding-bottom="0.0pt" start-indent="2.0pt" end-indent="2.0pt" padding-top="2.0pt" text-align="start" background-color="#ffffff" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt" font-weight="bold">
											<xsl:apply-templates select="TABLE_HEADER/ART_NUMBER" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell display-align="center" background-color="#ffffff">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" padding-bottom="0.0pt" start-indent="2.0pt" end-indent="2.0pt" padding-top="2.0pt" text-align="start" background-color="#ffffff" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt" font-weight="bold">
											<xsl:apply-templates select="TABLE_HEADER/DESCRIPTION" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell display-align="center" background-color="#ffffff">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" padding-bottom="0.0pt" start-indent="2.0pt" end-indent="2.0pt" padding-top="2.0pt" text-align="start" background-color="#ffffff" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt" font-weight="bold">
											<xsl:apply-templates select="TABLE_HEADER/ART_REFERENCE" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell display-align="center" background-color="#ffffff">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" padding-bottom="0.0pt" start-indent="2.0pt" end-indent="2.0pt" padding-top="2.0pt" text-align="start" background-color="#ffffff" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt" font-weight="bold">
											<xsl:apply-templates select="TABLE_HEADER/PIECES" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell display-align="center" background-color="#ffffff">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" padding-bottom="0.0pt" start-indent="2.0pt" end-indent="2.0pt" padding-top="2.0pt" text-align="end" background-color="#ffffff" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt" font-weight="bold">
											<xsl:apply-templates select="TABLE_HEADER/LIST_PRICE" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell display-align="center" background-color="#ffffff">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" padding-bottom="0.0pt" start-indent="2.0pt" end-indent="2.0pt" padding-top="2.0pt" text-align="end" background-color="#ffffff" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt" font-weight="bold">
											<xsl:apply-templates select="TABLE_HEADER/TOTAL_PRICE" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-header>
							<fo:table-body>
								<xsl:for-each select="ORDER_ENTRIES/ORDER_ENTRY">
									<xsl:variable name="altColor1">
										<xsl:if test="position() mod 2 = 1">#ffffff</xsl:if>
										<xsl:if test="position() mod 2 = 0">#b5cbe0</xsl:if>
									</xsl:variable>
									<fo:table-row border-color="#aaaaaa" border-style="solid" border-width="1.0pt" height="20.0pt">
										<fo:table-cell display-align="before">
											<fo:block white-space-collapse="false" linefeed-treatment="preserve" padding-bottom="2.0pt" start-indent="2.0pt" end-indent="2.0pt" padding-top="5.0pt" text-align="start" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
												<xsl:apply-templates select="PRODUCT_CODE" />
											</fo:block>
										</fo:table-cell>
										<fo:table-cell display-align="before">
											<fo:block white-space-collapse="false" linefeed-treatment="preserve" padding-bottom="2.0pt" start-indent="2.0pt" end-indent="2.0pt" padding-top="5.0pt" text-align="start" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt" overflow="hidden">
												<xsl:apply-templates select="PRODUCT_NAME" />
											</fo:block>
										</fo:table-cell>
										<fo:table-cell display-align="before">
											<fo:block white-space-collapse="false" linefeed-treatment="preserve" padding-bottom="2.0pt" start-indent="2.0pt" end-indent="2.0pt" padding-top="5.0pt" text-align="start" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
												<xsl:apply-templates select="PRODUCT_REFERENCE" />
											</fo:block>
										</fo:table-cell>
										<fo:table-cell display-align="before">
											<fo:block white-space-collapse="false" linefeed-treatment="preserve" padding-bottom="2.0pt" start-indent="2.0pt" end-indent="2.0pt" padding-top="5.0pt" text-align="start" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
												<xsl:apply-templates select="PIECES" />
											</fo:block>
										</fo:table-cell>
										<fo:table-cell display-align="before">
											<fo:block white-space-collapse="false" linefeed-treatment="preserve" padding-bottom="2.0pt" start-indent="2.0pt" end-indent="2.0pt" padding-top="5.0pt" text-align="end" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
												<xsl:apply-templates select="LIST_PRICE" />&#160;<xsl:apply-templates select="CURRENCY" />
											</fo:block>
										</fo:table-cell>
										<fo:table-cell display-align="before">
											<fo:block white-space-collapse="false" linefeed-treatment="preserve" padding-bottom="2.0pt" start-indent="2.0pt" end-indent="2.0pt" padding-top="5.0pt" text-align="end" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
												<xsl:apply-templates select="TOTAL_PRICE" />&#160;<xsl:apply-templates select="CURRENCY" />
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</xsl:for-each>

								<fo:table-row border-color="#aaaaaa" border-style="solid" border-width="1.0pt" border-bottom-width="0.0pt" height="5.0pt">
									<fo:table-cell number-columns-spanned="3" display-align="before"></fo:table-cell>
									<fo:table-cell number-columns-spanned="2" display-align="before"></fo:table-cell>
									<fo:table-cell display-align="before"></fo:table-cell>
								</fo:table-row>
								<fo:table-row border-color="#aaaaaa" border-style="solid" border-width="1.0pt" border-top-width="0.0pt" border-bottom-width="0.0pt" height="16.0pt">
									<fo:table-cell number-columns-spanned="3" display-align="before">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" start-indent="2.0pt" end-indent="2.0pt" text-align="start" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="2" display-align="before">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" start-indent="2.0pt" end-indent="2.0pt" text-align="start" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
											<xsl:apply-templates select="ORDER_ENTRIES/SUBTOTAL_LABEL" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell display-align="before">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" start-indent="2.0pt" end-indent="2.0pt" text-align="end" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
											<xsl:apply-templates select="ORDER_ENTRIES/SUBTOTAL" />&#160;<xsl:apply-templates select="ORDER_ENTRIES/CURRENCY" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row border-color="#aaaaaa" border-style="solid" border-width="1.0pt" border-top-width="0.0pt" border-bottom-width="0.0pt" height="16.0pt">
									<fo:table-cell number-columns-spanned="3" display-align="before">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" start-indent="2.0pt" end-indent="2.0pt" text-align="start" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="2" display-align="before">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" start-indent="2.0pt" end-indent="2.0pt" text-align="start" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
											<xsl:apply-templates select="ORDER_ENTRIES/DELIVERY_COST_LABEL" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell display-align="before">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" start-indent="2.0pt" end-indent="2.0pt" text-align="end" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
											<xsl:apply-templates select="ORDER_ENTRIES/DELIVERY_COST" />&#160;<xsl:apply-templates select="ORDER_ENTRIES/CURRENCY" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<xsl:if test="ORDER_ENTRIES/SHOW_PAYMENT_COST = 'show'">
									<fo:table-row border-color="#aaaaaa" border-style="solid" border-width="1.0pt" border-top-width="0.0pt" border-bottom-width="0.0pt"
										height="16.0pt">
										<fo:table-cell number-columns-spanned="3" display-align="before">
											<fo:block white-space-collapse="false" linefeed-treatment="preserve" start-indent="2.0pt" end-indent="2.0pt" text-align="start" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
											</fo:block>
										</fo:table-cell>
										<fo:table-cell number-columns-spanned="2" display-align="before">
											<fo:block white-space-collapse="false" linefeed-treatment="preserve" start-indent="2.0pt" end-indent="2.0pt" text-align="start" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
												<xsl:apply-templates select="ORDER_ENTRIES/PAYMENT_COST_LABEL" />
											</fo:block>
										</fo:table-cell>
										<fo:table-cell display-align="before">
											<fo:block white-space-collapse="false" linefeed-treatment="preserve" start-indent="2.0pt" end-indent="2.0pt" text-align="end" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
												<xsl:apply-templates select="ORDER_ENTRIES/PAYMENT_COST" />&#160;<xsl:apply-templates select="ORDER_ENTRIES/CURRENCY" />
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</xsl:if>
								<fo:table-row border-color="#aaaaaa" border-style="solid" border-width="1.0pt" border-top-width="0.0pt" border-bottom-width="0.0pt" height="16.0pt">
									<fo:table-cell number-columns-spanned="3" display-align="before">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" start-indent="2.0pt" end-indent="2.0pt" text-align="start" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="2" display-align="before">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" start-indent="2.0pt" end-indent="2.0pt" text-align="start" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
											<xsl:apply-templates select="ORDER_ENTRIES/TOTAL_TAX_LABEL" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell display-align="before">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" start-indent="2.0pt" end-indent="2.0pt" text-align="end" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
											<xsl:apply-templates select="ORDER_ENTRIES/TOTAL_TAX" />&#160;<xsl:apply-templates select="ORDER_ENTRIES/CURRENCY" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row border-color="#aaaaaa" border-style="solid" border-width="1.0pt" border-top-width="0.0pt">
									<fo:table-cell number-columns-spanned="3" display-align="before">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" start-indent="2.0pt" end-indent="2.0pt" text-align="start" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="2" display-align="before">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" start-indent="2.0pt" end-indent="2.0pt" text-align="start" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
											<xsl:apply-templates select="ORDER_ENTRIES/TOTAL_PRICE_LABEL" />
										</fo:block>
									</fo:table-cell>
									<fo:table-cell display-align="before">
										<fo:block white-space-collapse="false" linefeed-treatment="preserve" start-indent="2.0pt" end-indent="2.0pt" text-align="end" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
											<xsl:apply-templates select="ORDER_ENTRIES/TOTAL_PRICE" />&#160;<xsl:apply-templates select="ORDER_ENTRIES/CURRENCY" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>

					<xsl:if test="ORDER_INFO/ORDER_NOTE">
						<fo:block span="none" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
							<xsl:apply-templates select="ORDER_INFO/ORDER_NOTE_LABEL" />
						</fo:block>
						<fo:block span="none" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt">
							<xsl:apply-templates select="ORDER_INFO/ORDER_NOTE" />
						</fo:block>
					</xsl:if>

					<fo:block text-align="left" white-space-collapse="false" linefeed-treatment="preserve" span="none" color="#000000" font-family="Arial, Helvetica, SansSerif" font-size="10.0pt" padding-top="30.0pt">
						<xsl:apply-templates select="INFO_TEXT" />
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
</xsl:stylesheet>
