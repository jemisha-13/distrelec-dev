<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>

<spring:message code="compare.item.data.sheet" var="sDataSheet"/>
<spring:message code="toolsitem.add.cart.compare" var="sCartCompare"/>
<spring:message code="toolsitem.add.cart" var="sCartTitle"/>
<spring:message code="product.tabs.technical.attributes" var="sProductDetails"/>
<spring:message code="compare.item.attributes.other" var="sProductCompare"/>

<c:set var="phaseOut" value="false" />

<!-- Used by compare-list-item module !-->
<input type="hidden" class="hidden-product-code" value="${product.code}" />
<!-- End compare-list-item module !-->

<c:choose>
	<c:when test="${not empty product.endOfLifeDate or not product.buyable}">
		<%-- EOL PRODUCT --%>
		<div class="col-sm-12 g-item expired js-min-qty" data-id="${product.code}" data-min-qty="${product.price.minQuantity}">

			<%-- remove from compare button --%>
			<div class="pos-btn">
				<mod:toolsitem template="toolsitem-compare-remove" skin="compare-remove" tag="div" productId="${product.code}" />
			</div>

			<%-- product --%>
			<div class="hover-target">
				<mod:product template="compare-list-eol" skin="compare skin-product-expired" product="${product}"/>
			</div>

			<%-- alternative product link --%>
			<c:if test="${product.buyableReplacementProduct}">
				<div class="alternative-product">
					<div class="c-center">
						<div class="c-center-content">
							<a class="btn-secondary" href="<c:url value="${product.url}" />"><spring:message code="compare.item.choose.alternative" /></a>
						</div>
					</div>
				</div>
			</c:if>
		</div>
	</c:when>
	<c:otherwise>
		<%-- PRODUCT --%>
		<div class="col-sm-12 tableGrid g-item b-list-tools js-min-qty" data-id="${product.code}" data-min-qty="${product.price.minQuantity}">
			<%-- remove from compare button --%>

			<%-- product --%>
			<div class="hover-target">
				<mod:product template="compare-list" skin="compare" product="${product}" position="${position}"/>
			</div>

			<div class="compare-title__seperator">${sProductDetails}</div>

			<%-- bulk prices list --%>
			<mod:scaled-prices template="compare-list" skin="compare" product="${product}" />

			<%-- availability --%>
			<mod:shipping-information template="compare-page-list" product="${product}" skin="comparelist" />


			<%-- data sheets --%>
			<div class="download-constrain">
				<c:if test="${not empty product.downloads}">
					<c:forEach items="${product.downloads}" var="downloadSection">
						<c:if test="${downloadSection.code eq 'datasheets'}">
							<div class="download-constrain__title">${sDataSheet}</div>
							<c:forEach items="${downloadSection.downloads}" var="download" end="0">
								<div class="link-download-pdf cf download-constrain__main">
									<a href="${download.downloadUrl}" target="_blank" name="${download.name}">
										<i></i>
										<span class="filename ellipsis" title="${download.name}">${download.name}</span>
										<span class="ellipsis">
											(${download.mimeType},&nbsp;
											<c:forEach items="${download.languages}" var="language">${fn:toUpperCase(language.isocodePim)})</c:forEach>
										</span>
									</a>
								</div>
							</c:forEach>
						</c:if>
					</c:forEach>
				</c:if>
			</div>

			<div class="compare-title__seperator">${sProductCompare}</div>

			<%-- attributes  --%>
			<mod:detail-accordion-content template="technical-information-compare-list" skin="technical-information-compare" product="${product}" position="${position}" htmlClasses="compare-technical"/>

			<div class="tableGrid__product__btn">
				<a class="compare-cart ico-cart" title="${sCartTitle}"
				   data-aainteraction="add to cart"
				   data-product-code="${product.code}">${sCartCompare}</a>
			</div>

		</div><!--end g-item-->
	</c:otherwise>
</c:choose>
