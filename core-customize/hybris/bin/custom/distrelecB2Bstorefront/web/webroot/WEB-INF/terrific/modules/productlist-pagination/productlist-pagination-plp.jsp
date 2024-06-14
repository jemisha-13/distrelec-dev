<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<spring:message code="text.compact.view" var="sCompactView" />
<spring:message code="text.detailed.view" var="sDetailedView" />
<spring:message code="text.plp.view" var="sChangeView" />
<spring:message code="text.plp.filter.view" var="sFilterView" />
<spring:message code="text.plp.product.view" var="sProductView" />
<spring:message code="text.plp.filters.top" var="sTop" />
<spring:message code="text.plp.filters.sidebar" var="sSidebar" />

<div class="plp-pagination-wrapper">

	<div class="plp-pagination-wrapper__content">

		<fmt:formatNumber type="number" value="${searchPageData.pagination.totalNumberOfResults}" var="matchedProductsCount" />
		<c:if test="${fn:length(categoryDisplayDataList) > 1}">
			<c:choose>
				<c:when test="${currentLanguage.getIsocode() eq 'fr'}">
					<label class="plp-pagination-wrapper__content__title">
						<spring:theme code="text.plp.showing.of" arguments="${firstProductNumber},${lastProductNumber},${matchedProductsCount}"/>&nbsp;<spring:message code="text.products" />
					</label>
				</c:when>
				<c:otherwise>
					<label class="plp-pagination-wrapper__content__title">
						<spring:theme code="text.plp.showing.of" arguments="${firstProductNumber},${lastProductNumber}"/>&nbsp;${matchedProductsCount}&nbsp;<spring:message code="text.products" />
					</label>
				</c:otherwise>
			</c:choose>
		</c:if>

		<c:if test="${searchPageData.pagination.numberOfPages eq 1 }">
			<c:set var="hidePagination" value="hidden" />
		</c:if>

		<div class="pagination-wrapper ${hidePagination}">
			<c:set var="hasPreviousPage" value="${searchPageData.pagination.currentPage > 1}" />
			<c:set var="hasNextPage" value="${(searchPageData.pagination.currentPage + 1) <= searchPageData.pagination.numberOfPages}" />

			<%-- show btn-left --%>
			<c:if test="${hasPreviousPage}">
				<a data-aainteraction="pagination" id="pagination-prev=btn" href="${searchsearchPageData.pagination.prevUrl}" class="btn btn-left js-page-link" data-page-nr="${searchPageData.pagination.currentPage - 1}"><i class="fas fa-angle-left"></i></a>
			</c:if>

			<ul class="pagination">
				<c:if test="${not fn:endsWith(baseSearchURL, '?')}">
					<c:set var="baseSearchURL" value="${baseSearchURL}&amp;" />
				</c:if>
				<%-- display all pages without ellipsis in between --%>
				<c:if test="${searchPageData.pagination.numberOfPages <= 5}">
					<c:forEach var="i" begin="1" end="${searchPageData.pagination.numberOfPages}" step="1">
						<li><a data-aainteraction="pagination" href="${baseSearchURL}page=${i}" class ="js-page-link${i != searchPageData.pagination.currentPage ? '' : ' active'}" data-page-nr="${i}">${i}</a></li>
					</c:forEach>
				</c:if>

				<%-- display 5 pages with ellipsis in between --%>
				<c:if test="${searchPageData.pagination.numberOfPages > 5}">
					<c:choose>
						<c:when test="${searchPageData.pagination.currentPage <= 3}">
							<c:forEach var="i" begin="1" end="3" step="1">
								<li><a data-aainteraction="pagination" href="${baseSearchURL}page=${i}" class ="js-page-link${i != searchPageData.pagination.currentPage ? '' : ' active'}" data-page-nr="${i}">${i}</a></li>
							</c:forEach>
							<li class="dots">...</li>
							<li><a data-aainteraction="pagination" href="${searchPageData.pagination.lastUrl}" data-page-nr="${searchPageData.pagination.numberOfPages}" class ="js-page-link">${searchPageData.pagination.numberOfPages}</a></li>
						</c:when>

						<c:when test="${searchPageData.pagination.currentPage >= searchPageData.pagination.numberOfPages - 2}">
							<li><a data-aainteraction="pagination" href="${searchPageData.pagination.firstUrl}" data-page-nr="1" class ="js-page-link">1</a></li>
							<li data-aainteraction="pagination" class="dots">...</li>
							<c:forEach var="i" begin="${searchPageData.pagination.numberOfPages - 3}" end="${searchPageData.pagination.numberOfPages}" step="1">
								<li><a data-aainteraction="pagination" href="${baseSearchURL}page=${i}" class ="js-page-link${i != searchPageData.pagination.currentPage ? '' : ' active'}" data-page-nr="${i}">${i}</a></li>
							</c:forEach>
						</c:when>

						<c:otherwise>
							<c:if test="${searchPageData.pagination.numberOfPages > 6}">
								<li><a data-aainteraction="pagination" href="${searchPageData.pagination.firstUrl}" data-page-nr="1" class ="js-page-link">1</a></li>
								<li data-aainteraction="pagination" class="dots">...</li>
								<c:forEach var="i" begin="${searchPageData.pagination.currentPage - 1}" end="${searchPageData.pagination.currentPage + 1}" step="1">
									<li><a data-aainteraction="pagination" href="${baseSearchURL}page=${i}" class ="js-page-link${i != searchPageData.pagination.currentPage ? '' : ' active'}" data-page-nr="${i}">${i}</a></li>
								</c:forEach>
								<li data-aainteraction="pagination" class="dots">...</li>
								<li><a data-aainteraction="pagination" href="${searchPageData.pagination.lastUrl}" data-page-nr="${searchPageData.pagination.numberOfPages}" class ="js-page-link">${searchPageData.pagination.numberOfPages}</a></li>
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:if>
			</ul>

			<%-- show btn-right --%>
			<c:if test="${hasNextPage}">
				<a data-aainteraction="pagination" id="pagination-next=btn" href="${searchPageData.pagination.nextUrl}" class="btn btn-right js-page-link" data-page-nr="${searchPageData.pagination.currentPage + 1}"> <spring:message code="text.next" />  <i class="fas fa-angle-right"></i></a>
			</c:if>
		</div>
	</div>

	<div class="plp-pagination-wrapper__product-action-bar">

		<div class="plp-pagination-wrapper__product-action-bar__view-toggle">

			<div class="plp-viewlist-dropdown" data-aainteraction="change view type">
				<button id="plp-viewlist-dropbtn" class="plp-viewlist-dropdown__dropbtn"> ${sChangeView} <i class="fas fa-angle-down plp-viewlist-dropdown__dropbtn__arrow"></i></button>
				<div class="plp-viewlist-dropdown__content">
				<div id="plp-filterview" class="plp-filterview-content">
					<span class="plp-viewlist-dropdown__filterview">${sFilterView}</span>
					<input id="plp-viewlist-sidebarbtn" name="filterView" class="plp-viewlist-dropdown__content__sidebarbtn" type="radio"><label class="view-label" for="plp-viewlist-sidebarbtn">${sSidebar}</label>

					<input id="plp-viewlist-topbtn" name="filterView" class="plp-viewlist-dropdown__content__topbtn" type="radio"><label class="view-label" for="plp-viewlist-topbtn"> ${sTop} </label>

				</div>
				<div id="plp-productview" class="plp-productview-content">
					<span class="plp-viewlist-dropdown__productview">${sProductView}</span>
					<input id="plp-viewlist-compactbtn" name="productView" class="plp-viewlist-dropdown__content__compactbtn" type="radio" data-compact-view="${sCompactView}"><label class="view-label" for="plp-viewlist-compactbtn">${sCompactView}</label>

					<input id="plp-viewlist-detailedbtn" name="productView" class="plp-viewlist-dropdown__content__detailedbtn" type="radio" data-detailed-view="${sDetailedView}"><label class="view-label" for="plp-viewlist-detailedbtn">${sDetailedView}</label>
				</div>

				</div>
			</div>

		</div>

		<mod:productlist-order  template="plp" skin="plp"/>

		<div class="gu-3--${currentCountry.isocode} plp-pagination-wrapper__product-action-bar__product-per-page">
			<label for="productlist-pagination"><spring:message code="product.list.show.perPage" /></label>
			<select id="select-productlist-pagination" name="productlist-pagination" class="selectpicker selectboxit-meta js-page-size" id="productlist-pagination">

				<c:forEach items="${searchPageData.pagination.productsPerPageOptions}" var="productsPerPageOption">
					<option value="${productsPerPageOption.value}" ${productsPerPageOption.selected ? 'selected="selected"' : ''}>${productsPerPageOption.value}</option>
				</c:forEach>

			</select>
		</div>

	</div>

</div>
