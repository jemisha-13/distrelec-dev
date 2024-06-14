<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<spring:message code="text.store.dateformat" var="datePattern" />

<ul>
	<c:if test="${not empty product.productInformation}">
		<%-- NOTES --%>
		<c:if test="${not empty product.productInformation.orderNote || not empty product.productInformation.orderNoteArticle ||
						not empty product.productInformation.deliveryNote || not empty product.productInformation.deliveryNoteArticle ||
						not empty product.productInformation.assemblyNote || not empty product.productInformation.usageNote}">
			<li>
				<h3 class="base"><spring:message code="detailaccordionproductinformation.notes" text="Notes" /></h3>
				<table>
					<tbody>
						<c:if test="${not empty product.productInformation.orderNote || not empty product.productInformation.orderNoteArticle}">
							<tr>
								<td valign="top"><spring:message code="detailaccordionproductinformation.notes.order" text="Order Note" /></td>
								<td>
									<c:if test="${not empty product.productInformation.orderNote}">
										<p>${product.productInformation.orderNote}</p>
									</c:if>
									<c:if test="${not empty product.productInformation.orderNoteArticle}">
										<p>${product.productInformation.orderNoteArticle}</p>
									</c:if>
								</td>
							</tr>
						</c:if>
						<c:if test="${not empty product.productInformation.deliveryNote || not empty product.productInformation.deliveryNoteArticle}">
							<tr>
								<td valign="top"><spring:message code="detailaccordionproductinformation.notes.delivery" text="Delivery Note" /></td>
								<td>
									<c:if test="${not empty product.productInformation.deliveryNote}">
										<p>${product.productInformation.deliveryNote}</p>
									</c:if>
									<c:if test="${not empty product.productInformation.deliveryNoteArticle}">
										<p>${product.productInformation.deliveryNoteArticle}</p>
									</c:if>
								</td>
							</tr>
						</c:if>
						<c:if test="${not empty product.productInformation.assemblyNote}">
							<tr>
								<td valign="top"><spring:message code="detailaccordionproductinformation.notes.assembly" text="Assembly Note" /></td>
								<td>
									${product.productInformation.assemblyNote}
								</td>
							</tr>
						</c:if>
						<c:if test="${not empty product.productInformation.usageNote}">
							<tr>
								<td valign="top"><spring:message code="detailaccordionproductinformation.notes.usage" text="Usage Note" /></td>
								<td>
									${product.productInformation.usageNote}
								</td>
							</tr>
						</c:if>
					</tbody>
				</table>
			</li>
		</c:if>

		<%-- FAMILY INFORMATION --%>
		<c:if test="${not empty product.productInformation.familyDescription || not empty product.productInformation.familyDescriptionBullets || not empty product.productInformation.seriesDescription || not empty product.productInformation.seriesDescriptionBullets}">
			<li>
				<h3 class="base"><spring:message code="detailaccordionproductinformation.family.information" text="Family Information" /></h3>
				<table>
					<tbody>
						<c:if test="${not empty product.productInformation.familyDescription || not empty product.productInformation.familyDescriptionBullets}">
							<tr>
								<td valign="top"><spring:message code="detailaccordionproductinformation.family.description" text="Family Description" /></td>
								<td>
									<c:forEach items="${product.productInformation.familyDescription}" var="paragraph">
										<p>${paragraph}</p>
									</c:forEach>
									<ul>
										<c:forEach items="${product.productInformation.familyDescriptionBullets}" var="bullet">
											<li>${bullet}</li>
										</c:forEach>
									</ul>
								</td>
							</tr>
						</c:if>
						<c:if test="${not empty product.productInformation.seriesDescription || not empty product.productInformation.seriesDescriptionBullets}">
							<tr>
								<td valign="top"><spring:message code="detailaccordionproductinformation.series.description" text="Series Description" /></td>
								<td>
									<c:forEach items="${product.productInformation.seriesDescription}" var="paragraph">
										<p>${paragraph}</p>
									</c:forEach>
									<ul>
										<c:forEach items="${product.productInformation.seriesDescriptionBullets}" var="bullet">
											<li>${bullet}</li>
										</c:forEach>
									</ul>
								</td>
							</tr>
						</c:if>
					</tbody>
				</table>
			</li>
		</c:if>

		<%-- ARTICLE INFORMATION --%>
		<c:if test="${not empty product.productInformation.articleDescription || not empty product.productInformation.articleDescriptionBullets || not empty product.productInformation.paperCatalogPageNumber}">
			<li>
				<h3 class="base"><spring:message code="detailaccordionproductinformation.article.information" text="Article Information" /></h3>
				<table>
					<tbody>
						<c:if test="${not empty product.productInformation.articleDescription || not empty product.productInformation.articleDescriptionBullets}">
							<tr>
								<td valign="top"><spring:message code="detailaccordionproductinformation.article.description" text="Article Description" /></td>
								<td>
									<c:forEach items="${product.productInformation.articleDescription}" var="paragraph">
										<p>${paragraph}</p>
									</c:forEach>
									<ul>
										<c:forEach items="${product.productInformation.articleDescriptionBullets}" var="bullet">
											<li>${bullet}</li>
										</c:forEach>
									</ul>
								</td>
							</tr>
						</c:if>
						<c:if test="${not empty product.productInformation.paperCatalogPageNumber}">
							<tr>
								<td valign="top"><spring:message code="detailtabsproductinformation.article.paperCatalogPageNumber.label" text="Paper catalogues" /></td>
								<td>
									<spring:message code="detailtabsproductinformation.article.paperCatalogPageNumber.value"  text="2013/14 p. {0}" arguments="${product.productInformation.paperCatalogPageNumber}" />
								</td>
							</tr>
						</c:if>
						<c:if test="${not empty product.productInformation.paperCatalogPageNumber_16_17}">
							<tr>
								<td valign="top"><spring:message code="detailtabsproductinformation.article.paperCatalogPageNumber.label" text="Paper catalogues" /></td>
								<td>
									<spring:message code="detailtabsproductinformation.article.paperCatalogPageNumber_16_17.value"  text="2016/17 p. {0}" arguments="${product.productInformation.paperCatalogPageNumber_16_17}" />
								</td>
							</tr>
						</c:if>
					</tbody>
				</table>
			</li>
		</c:if>
	</c:if>
	
	<%-- ENVIRONMENTAL INFORMATION --%>
	<c:if test="${not empty product.rohs || not empty product.svhc || not empty product.svhcReviewDate}">
		<li>
			<h3 class="base"><spring:message code="detailaccordionproductinformation.environmental.information" /></h3>
			<table>
				<tbody>
					<c:if test="${not empty product.rohs}">
						<tr>
							<td><spring:message code="detailaccordionproductinformation.rohs" /></td>
							<td>
								${product.rohs}
							</td>
						</tr>
					</c:if>
					<c:if test="${not empty product.svhc}">
						<tr>
							<td><spring:message code="detailaccordionproductinformation.svhc" /></td>
							<td>
								${product.svhc}
							</td>
						</tr>
					</c:if>
					<c:if test="${not empty product.svhcReviewDate}">
						<tr>
							<td><spring:message code="detailaccordionproductinformation.svhc.reviewDate" /></td>
							<td>
								<fmt:formatDate value="${product.svhcReviewDate}" type="date" dateStyle="short" pattern="${datePattern}" />
							</td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</li>
	</c:if>

	<%-- ADDITIONAL INFORMATION --%>
	<c:if test="${not empty product.countryOfOrigin || not empty product.transportGroup || not empty product.distManufacturer || not empty product.unspsc5 || (not empty product.grossWeight && not empty product.grossWeightUnit) || not empty product.dimensions || not empty product.originalPackSize || not empty product.originalPackSize}">
		<li>
			<h3 class="base"><spring:message code="detailaccordionproductinformation.additional.information" /></h3>
			<table>
				<tbody>
					<c:if test="${not empty product.countryOfOrigin}">
						<tr>
							<td><spring:message code="detailaccordionproductinformation.country.of.origin" /></td>
							<td>
								${product.countryOfOrigin.name} (${product.countryOfOrigin.isocode})
							</td>
						</tr>
					</c:if>
					<c:if test="${ not empty product.transportGroup }">
						<tr>
							<td><spring:message code="detailaccordionproductinformation.transport.group" /></td>
							<td>
								${product.transportGroup}
							</td>
						</tr>
					</c:if>
					<c:if test="${ not empty product.distManufacturer }">
						<tr>
							<td><spring:message code="detailaccordionproductinformation.manufacturer" /></td>
							<td>
								<span itemprop="brand">${product.distManufacturer.name}</span>
							</td>
						</tr>
					</c:if>
					<c:if test="${not empty product.grossWeight && not empty product.grossWeightUnit}">
						<tr>
							<td><spring:message code="detailtabsproductinformation.grossWeight" /></td>
							<td>${product.grossWeight}&nbsp;${product.grossWeightUnit}</td>
						</tr>
					</c:if>
					<c:if test="${not empty product.dimensions}">
						<tr>
							<td><spring:message code="detailtabsproductinformation.dimensions" /></td>
							<td>${product.dimensions}</td>
						</tr>
					</c:if>
					<c:if test="${not empty product.originalPackSize}">
						<tr>
							<td><spring:message code="detailtabsproductinformation.originalPackSize" /></td>
							<td>${product.originalPackSize}</td>
						</tr>
					</c:if>
					<c:if test="${not empty product.customsCode}">
						<tr>
							<td><spring:message code="detailtabsproductinformation.customsCode" /></td>
							<td>${product.customsCode}</td>
						</tr>
					</c:if>
					<c:if test="${not empty product.unspsc5}">
						<tr>
							<td><spring:message code="detailtabsproductinformation.unspsc5" /></td>
							<td>${product.unspsc5}</td>
						</tr>
					</c:if>
					<c:if test="${not empty product.ean}">
						<tr>
							<td><spring:message code="detailtabsproductinformation.ean" /></td>
							<td>${product.ean}</td>
						</tr>
					</c:if>
					<c:if test="${not empty product.enumber}">
						<tr>
							<td><spring:message code="detailtabsproductinformation.enumber" /></td>
							<td>${product.enumber}</td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</li>
	</c:if>
</ul>
