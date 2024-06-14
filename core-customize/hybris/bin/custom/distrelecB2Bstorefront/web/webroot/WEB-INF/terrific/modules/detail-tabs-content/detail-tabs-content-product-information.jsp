<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<spring:message code="text.store.dateformat" var="datePattern" />

<ul class="list-holder">
	<c:if test="${not empty product.productInformation}">
		<%-- NOTES --%>
		<c:if test="${not empty product.productInformation.orderNote || not empty product.productInformation.orderNoteArticle ||
						not empty product.productInformation.deliveryNote || not empty product.productInformation.deliveryNoteArticle ||
						not empty product.productInformation.assemblyNote || not empty product.productInformation.usageNote}">
			<li class="list-holder__item">
				<h3 class="base"><spring:message code="detailtabsproductinformation.notes" text="Notes" /></h3>
				<c:if test="${not empty product.productInformation.orderNote || not empty product.productInformation.orderNoteArticle}">
					<div class="holder-content">
						<span class="title"><spring:message code="detailtabsproductinformation.notes.order" text="Order Note" /></span>
						<c:if test="${not empty product.productInformation.orderNote}">
							<p>${product.productInformation.orderNote}</p>
						</c:if>
						<c:if test="${not empty product.productInformation.orderNoteArticle}">
							<p>${product.productInformation.orderNoteArticle}</p>
						</c:if>
					</div>
				</c:if>
				<c:if test="${not empty product.productInformation.deliveryNote || not empty product.productInformation.deliveryNoteArticle}">
					<div class="holder-content">
						<span class="title"><spring:message code="detailtabsproductinformation.notes.delivery" text="Delivery Note" /></span>
						<c:if test="${not empty product.productInformation.deliveryNote}">
							<p>${product.productInformation.deliveryNote}</p>
						</c:if>
						<c:if test="${not empty product.productInformation.deliveryNoteArticle}">
							<p>${product.productInformation.deliveryNoteArticle}</p>
						</c:if>
					</div>
				</c:if>
				<c:if test="${not empty product.productInformation.assemblyNote}">
					<div class="holder-content">
						<span class="title"><spring:message code="detailtabsproductinformation.notes.assembly" text="Assembly Note" /></span>
						${product.productInformation.assemblyNote}
					</div>
				</c:if>
				<c:if test="${not empty product.productInformation.usageNote}">
					<div class="holder-content usageNote">
						<span class="title"><spring:message code="detailtabsproductinformation.notes.usage" text="Usage Note" /></span>
						<p class="usageNote__text">${product.productInformation.usageNote}</p>
					</div>
				</c:if>
			</li>
		</c:if>

		<%-- FAMILY INFORMATION --%>
		<c:if test="${not empty product.productInformation.familyDescription || not empty product.productInformation.familyDescriptionBullets || not empty product.productInformation.seriesDescription || not empty product.productInformation.seriesDescriptionBullets}">
			<li class="list-holder__item list-holder__item--family-list">
				<h3 class="base"><spring:message code="detailtabsproductinformation.family.information" text="Family Information" /></h3>
				<c:if test="${not empty product.productInformation.familyDescription || not empty product.productInformation.familyDescriptionBullets}">
					<div class="holder-content">
						<c:forEach items="${product.productInformation.familyDescription}" var="paragraph">
							<p>${paragraph}</p>
						</c:forEach>
						<ul>
							<c:forEach items="${product.productInformation.familyDescriptionBullets}" var="bullet">
								<li>${bullet}</li>
							</c:forEach>
						</ul>
					</div>
				</c:if>
				<c:if test="${not empty product.productInformation.seriesDescription || not empty product.productInformation.seriesDescriptionBullets}">
					<div class="holder-content">
						<span class="title"><spring:message code="detailtabsproductinformation.series.description" text="Series Description" /></span>
						<c:forEach items="${product.productInformation.seriesDescription}" var="paragraph">
							<p>${paragraph}</p>
						</c:forEach>
						<ul>
							<c:forEach items="${product.productInformation.seriesDescriptionBullets}" var="bullet">
								<li>${bullet}</li>
							</c:forEach>
						</ul>
					</div>
				</c:if>
			</li>
		</c:if>

		<%-- ARTICLE INFORMATION --%>
		<c:if test="${not empty product.productInformation.articleDescription || not empty product.productInformation.articleDescriptionBullets || not empty product.productInformation.paperCatalogPageNumber}">
			<li class="list-holder__item list-holder__item--article-list">
				<h3 class="base"><spring:message code="detailtabsproductinformation.article.information" text="Article Information" /></h3>
				<c:if test="${not empty product.productInformation.articleDescription || not empty product.productInformation.articleDescriptionBullets}">
					<div class="holder-content">
						<c:forEach items="${product.productInformation.articleDescription}" var="paragraph">
							<p>${paragraph}</p>
						</c:forEach>
						<ul>
							<c:forEach items="${product.productInformation.articleDescriptionBullets}" var="bullet">
								<li>${bullet}</li>
							</c:forEach>
						</ul>
					</div>
				</c:if>

				<!-- DISTRELEC-8887: Display "old" article number on product detail page  -->
				<c:choose>
					<c:when test="${currentCountry.isocode eq 'CH' || currentCountry.isocode eq 'AT' || currentCountry.isocode eq 'IT' || currentCountry.isocode eq 'CZ'
									 || currentCountry.isocode eq 'HU' || currentCountry.isocode eq 'RO' || currentCountry.isocode eq 'SK'}" >
						<div class="holder-content">
							<span class="title"><spring:message code="detailtabsproductinformation.previousarticlenumber" />:</span> ${product.movexArticleNumber}
						</div>
					</c:when>
					<c:when test="${currentCountry.isocode eq 'DK' || currentCountry.isocode eq 'FI' || currentCountry.isocode eq 'NO' || currentCountry.isocode eq 'SE'
									 || currentCountry.isocode eq 'LT' || currentCountry.isocode eq 'LV' || currentCountry.isocode eq 'EE' || currentCountry.isocode eq 'NL' 
									 || currentCountry.isocode eq 'PL' || currentCountry.isocode eq 'EX'}" >
						<div class="holder-content">
							<span class="title"><spring:message code="detailtabsproductinformation.previousarticlenumber" />:</span> ${product.elfaArticleNumber}
						</div>
					</c:when>
					<c:when test="${currentCountry.isocode eq 'DE'}">
						<div class="holder-content">
							<span class="title"><spring:message code="detailtabsproductinformation.previousarticlenumber" />:</span> ${product.navisionArticleNumber}
						</div>
					</c:when>
					<c:otherwise>
					</c:otherwise>
				</c:choose>
				<c:if test="${not empty product.productInformation.paperCatalogPageNumber_16_17}">
					<div class="holder-content">
						<span class="title"><spring:message code="detailtabsproductinformation.article.paperCatalogPageNumber.label" text="Paper catalogues" />:</span>
						<spring:message code="detailtabsproductinformation.article.paperCatalogPageNumber_16_17.value" text="2016/17 p. {0}" arguments="${product.productInformation.paperCatalogPageNumber_16_17}" />
					</div>
				</c:if>

				<c:if test="${not empty product.productInformation.paperCatalogPageNumber}">
					<div class="holder-content">
						<span class="title"><spring:message code="detailtabsproductinformation.article.paperCatalogPageNumber.label" text="Paper catalogues" />:</span>
						<spring:message code="detailtabsproductinformation.article.paperCatalogPageNumber.value" text="2013/14 p. {0}" arguments="${product.productInformation.paperCatalogPageNumber}" />
					</div>
				</c:if>

			</li>
		</c:if>
	</c:if>

	<%-- ENVIRONMENTAL INFORMATION --%>
	<c:if test="${not empty product.rohs || not empty product.svhc || not empty product.svhcReviewDate || isDangerousGoods eq true}">
		<li class="list-holder__item list-holder__item--environment-list">
			<h3 class="base">
				<spring:message code="detailtabsproductinformation.environmental.information" />

				<c:if test="${isDangerousGoods eq true}" >
					<i class="fa fa-exclamation-triangle" aria-hidden="true"></i>
				</c:if>
			</h3>

			<c:if test="${isProductBatteryCompliant}">
				<div class="holder-content is-battery">
					<div class="title"><spring:message code="detailtabsproductinformation.battery"/></div>
					<div><spring:message code="detailtabsproductinformation.battery.compliant"/></div>
					<div><spring:message code="detailtabsproductinformation.battery.compliant.description"/></div>
					<a class="link"
					   title="<spring:message code="detailtabsproductinformation.battery.compliant.url" text="Order Note" />"
					   href="#"
					   onclick="window.open('/compliance-document/document/Battery_Compliance.pdf');return false;"
					   data-aainteraction="file download" data-file-type="pdf"
					   data-file-name="Battery_Compliance.pdf"
					   id="battery-recycling-pdf-link" target="_blank"><spring:message
							code="detailtabsproductinformation.battery.compliant.url"/></a>
				</div>
			</c:if>

			<div class="holder-content flex-holder">
				<span class="title"><spring:message code="detailtabsproductinformation.rohs" /></span>
				<span id="ROHS-title" class="ROHS-title">
					<c:choose>
						<c:when test="${isROHSComplaint}">
							<spring:message code="detailtabsproductinformation.rohs.compliant" />
						</c:when>
                        <c:when test="${product.rohsCode eq '99'}">
                            <spring:message code="rohs.underReview" />. <a class="link js-openRohsAdditionalInformationModal" href="#"><spring:message code="rohs.underReview.learnMore" /></a>
                        </c:when>
						<c:when test="${product.rohsCode eq '13'}">
							<spring:message code="rohs.notCompliant" />. <a class="link js-openRohsAdditionalInformationModal" href="#"><spring:message code="rohs.underReview.learnMore" /></a>
						</c:when>
						<c:when test="${product.rohsCode eq '11'}">
							<spring:message code="rohs.notApplicable" />. <a class="link js-openRohsAdditionalInformationModal" href="#"><spring:message code="rohs.underReview.learnMore" /></a>
						</c:when>
                        <c:otherwise>
							${product.rohs}
						</c:otherwise>
					</c:choose>
				</span>
			</div>
			<c:choose>
				<c:when test="${isRNDProduct}">
					<c:if test="${isROHSConform}">
						<div class="holder-content flex-holder">
							<span class="title"></span>
							<a class="link" title="<spring:message code="detailtabsproductinformation.rohs.linkname" text="Order Note" />"
							   href="#"
							   onclick="window.open('/compliance-document/pdf/ROHS_${product.code}');return false;"
							   data-aainteraction="file download" data-file-type="pdf" data-file-name="ROHS_${product.code}.pdf"
							   id="ROHS-pdf-link" target="_blank"><spring:message code="detailtabsproductinformation.rohs.linkname" /></a>
						</div>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:if test="${not empty product.rohs and isROHSValidForCountry}">
						<div class="holder-content flex-holder">
							<span class="title"></span>
							<a class="link" title="<spring:message code="detailtabsproductinformation.rohs.linkname" text="Order Note" />"
							   href="#"
							   onclick="window.open('/compliance-document/pdf/ROHS_${product.code}');return false;"
							   data-aainteraction="file download" data-file-type="pdf" data-file-name="ROHS_${product.code}.pdf"
							   id="ROHS-pdf-link" target="_blank"><spring:message code="detailtabsproductinformation.rohs.linkname" /></a>
						</div>
					</c:if>
				</c:otherwise>
			</c:choose>
			<c:if test="${not empty product.hasSvhc}">
				<c:choose>
					<c:when test="${product.hasSvhc eq true}">
						<spring:message code="reach.regulation.named" var="reachRegulationName"/>
						<spring:message code="reach.svhc.link" var="svhcLinkText"/>
					</c:when>
					<c:otherwise>
						<spring:message code="reach.regulation.no.svhc" var="reachRegulationName"/>
						<spring:message code="reach.noSvhc.link" var="svhcLinkText"/>
					</c:otherwise>
				</c:choose>
				<div class="holder-content flex-holder">
					<span id="REACH-id flex-holder" class="title"><spring:message code="reach.regulation.title" /></span>
					<span id="REACH-title-id">${reachRegulationName}</span>
				</div>
				<div class="holder-content flex-holder">
					<span id="REACH-id-empty" class="title title__empty">&nbsp;</span>
					<a class="link" onclick="window.open('/compliance-document/pdf/SVHC_${product.code}');return false;" data-aainteraction="file download" data-file-type="pdf" data-file-name="SVHC_${product.code}.pdf" target="_blank" title="${svhcLinkText}" href="/compliance-document/pdf/SVHC_${product.code}">${svhcLinkText}</a>
				</div>
			</c:if>

			<c:set var="IsBlank_Unkown_True" value="${empty product.hasSvhc || product.hasSvhc}" />
            <c:if test="${not empty product.svhc and IsBlank_Unkown_True}">
                <div class="holder-content flex-holder">
                    <span class="title title__additional-data"><spring:message code="detailtabsproductinformation.svhc" /></span>
                     <span class="svhc-additional-data">
							 ${product.svhc}
					 </span>
                </div>
                <c:if test="${not empty product.svhcURL and IsBlank_Unkown_True}">
                    <div class="holder-content flex-holder">
                        <span class="title"><spring:message code="detailtabsproductinformation.svhc.url" text="SVHC URL" /></span>
                        <a class="link" title="${product.svhcURL}" href="${product.svhcURL}" target="_blank">${product.svhcURL}</a>
                    </div>
                </c:if>
            </c:if>

			<c:if test="${product.hasSvhc and not empty product.scip}">
				<div class="holder-content flex-holder">
					<span id="SCIP-title" class="title"><spring:message code="product.scip.label"/></span>
					<span id="SCIP-code">${product.scip}</span>
				</div>
			</c:if>

			<c:if test="${product.transportGroupData.dangerous}">
				<div class="holder-content">
					<span class="warntext dangerous"><i class="dangerous-icon"></i><spring:message code="product.message.dangerous" /></span>
				</div>
			</c:if>
			<c:if test="${product.transportGroupData.bulky}">
				<div class="holder-content">
					<span class="warntext bulky"><i class="bulky-icon"></i><spring:message code="product.message.bulky" /></span>
				</div>
			</c:if>
			<c:if test="${not empty product.dangerousSubstancesDirective}">
				<div class="holder-content" title="<spring:message code="detailtabsproductinformation.dangerousSubstancesDirective.title" text="" />">
					<span class="title"><spring:message code="detailtabsproductinformation.dangerousSubstancesDirective" /></span>
					${product.dangerousSubstancesDirective}
				</div>
			</c:if>
			<c:if test="${not empty product.ghsImages}">
				<div class="holder-content holder-content--ghs row">
					<c:forEach var="url" items="${product.ghsImages}">
						<div class="holder-content__ghs-image col-2">
							<img class="img-fluid" alt="GHS Image" src=${url} />
						</div>
					</c:forEach>
				</div>
			</c:if>
			<c:if test="${not empty product.signalWord}">
				<div class="holder-content holder-content--signal-word">
					<span class="title">
						<spring:message code="detailtabsproductinformation.signalWord" />
					</span>
					<span class="info">
						<br /> <spring:message code="detailtabsproductinformation.signalWord.${fn:toLowerCase(product.signalWord)}" />
					</span>
				</div>
			</c:if>
		</li>
	</c:if>

	<%-- ADDITIONAL INFORMATION --%>
	<c:if test="${not empty product.countryOfOrigin || not empty product.transportGroup || not empty product.distManufacturer || not empty product.unspsc5 || (not empty product.grossWeight && not empty product.grossWeightUnit) || not empty product.dimensions || not empty product.originalPackSize || not empty product.originalPackSize}">
		<li class="list-holder__item list-holder__item--additional-list">
			<h3 class="base"><spring:message code="detailtabsproductinformation.additional.information" /></h3>
			<c:if test="${not empty product.countryOfOrigin}">
				<div class="holder-content">
					<span class="title"><spring:message code="detailtabsproductinformation.country.of.origin" /></span>
					${product.countryOfOrigin.name} (${product.countryOfOrigin.isocode})
				</div>
			</c:if>
			<c:if test="${ not empty product.transportGroup }">
				<div class="holder-content">
					<span class="title"><spring:message code="detailtabsproductinformation.transport.group" /></span>
					${product.transportGroup}
				</div>
			</c:if>
			<c:if test="${ not empty product.distManufacturer }">
				<div class="holder-content">
					<span class="title"><spring:message code="detailtabsproductinformation.manufacturer" /></span>
						<span itemprop="brand">
							<c:set var="targetDetailPage" scope="page" value="/${currentLanguage.isocode}/manufacturer/${product.distManufacturer.nameSeo}/${product.distManufacturer.code}"></c:set>
							<a class="link" href='${targetDetailPage}'>
								${product.distManufacturer.name}
							</a>
						</span>
				</div>
			</c:if>
			<c:if test="${not empty product.grossWeight && not empty product.grossWeightUnit}">
				<div class="holder-content">
					<span class="title"><spring:message code="detailtabsproductinformation.grossWeight" /></span>
					${product.grossWeight}&nbsp;${product.grossWeightUnit}
				</div>
			</c:if>
			<c:if test="${not empty product.dimensions}">
				<div class="holder-content">
					<span class="title"><spring:message code="detailtabsproductinformation.dimensions" /></span>
					${product.dimensions}
				</div>
			</c:if>
			<c:if test="${not empty product.originalPackSize}">
				<div class="holder-content">
					<span class="title"><spring:message code="detailtabsproductinformation.originalPackSize" /></span>
					${product.originalPackSize}
				</div>
			</c:if>
			<c:if test="${not empty product.customsCode}">
				<div class="holder-content">
					<span class="title"><spring:message code="detailtabsproductinformation.customsCode" /></span>
					${product.customsCode}
				</div>
			</c:if>
			<c:if test="${not empty product.unspsc5}">
				<div class="holder-content">
					<span class="title"><spring:message code="detailtabsproductinformation.unspsc5" /></span>
					${product.unspsc5}
				</div>
			</c:if>
		 	<c:if test="${not empty product.ean}">
				<div class="holder-content">
					<span class="title"><spring:message code="detailtabsproductinformation.ean" /></span>
					${product.ean}
				</div>
			</c:if>
			<c:if test="${not empty product.enumber}">
				<div class="holder-content">
					<span class="title"><spring:message code="detailtabsproductinformation.enumber" /></span>
					${product.enumber}
				</div>
			</c:if>
		</li>
	</c:if>

	<%-- HAZARDOUS STATEMENTS AND INFORMATION --%>
	<li class="list-holder__item list-holder__item--hazardous-list">
		<c:if test="${not empty product.hazardStatements}">
		<div class="holder-content holder-content--haz-stat">
					<span class="title">
						<spring:message code="detailtabsproductinformation.hazardStatements" />
					</span>
			<ul>
				<c:forEach var="statement" items="${product.hazardStatements}">
					<c:forEach var="data" items="${hazardStatementData}">
						<c:if test="${statement eq data.code}">
							<li>
								<div class="hazardous-content">
									<div>${data.code}:</div>
									<div class="description">${fn:escapeXml(data.description)}</div>
								</div>
							</li>
						</c:if>
					</c:forEach>

				</c:forEach>
			</ul>
		</div>
		</c:if>

		<c:if test="${not empty product.supplementalHazardInfos}">
		<div class="holder-content holder-content--sup-haz-info">
					<span class="title">
						<spring:message code="detailtabsproductinformation.supplementalhazardInfo" />
					</span>
			<ul>
				<c:forEach var="info" items="${product.supplementalHazardInfos}">
					<c:forEach var="data" items="${SuppHazardInfoData}">
						<c:if test="${info eq data.code}">
							<li>
								<div class="hazardous-content">
									<div>${data.code}:</div>
									<div class="description">${fn:escapeXml(data.description)}</div>
								</div>
							</li>
						</c:if>
					</c:forEach>
				</c:forEach>

				<c:if test="${businessOnlyProduct}">
					<div class="holder-content">
						<span class="title"><spring:message code="detailtabsproductinformation.businessonlyproduct" /></span>
					</div>
				</c:if>
			</ul>
		</div>
			</c:if>

	 <c:if test="${not empty metaData.contentTitle}">
		<li class="list-holder__item">
			<h3 class="base">${metaData.contentTitle}</h3>
			<div class="metahd-content-description">
				${metaData.contentDescription}
			</div>
		</li>
	</c:if>
</ul>

<mod:lightbox-rohs-additional-information rohsCode="${product.rohsCode}"/>
