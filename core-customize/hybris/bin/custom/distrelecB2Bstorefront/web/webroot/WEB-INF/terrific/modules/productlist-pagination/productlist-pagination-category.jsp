<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="row">
	<div class="col gu-3 gu-3--${currentCountry.isocode}">
		<label for="productlist-pagination"><spring:message code="product.list.show.perPage" /></label>
		<select id="select-productlist-pagination" name="productlist-pagination" class="selectpicker selectboxit-meta js-page-size" id="productlist-pagination">

			<c:forEach items="${searchPageData.pagination.productsPerPageOptions}" var="productsPerPageOption">
				<option value="${productsPerPageOption.value}" ${productsPerPageOption.selected ? 'selected="selected"' : ''}>${productsPerPageOption.value}</option>
			</c:forEach>

		</select>
	</div>
	<div class="col pull-right">
		<div class="pagination-wrapper">

			<c:set var="hasPreviousPage" value="${searchPageData.pagination.currentPage > 1}" />
			<c:set var="hasNextPage" value="${(searchPageData.pagination.currentPage + 1) <= searchPageData.pagination.numberOfPages}" />

			<%-- show btn-left --%>
			<c:if test="${hasPreviousPage}">
				<a href="${searchsearchPageData.pagination.prevUrl}" class="btn btn-left js-page-link" data-page-nr="${searchPageData.pagination.currentPage - 1}"><i></i></a>
			</c:if>

			<ul class="pagination">
				<c:if test="${not fn:endsWith(baseSearchURL, '?')}">
					<c:set var="baseSearchURL" value="${baseSearchURL}&amp;" />
				</c:if>
				<%-- display all pages without ellipsis in between --%>
				<c:if test="${searchPageData.pagination.numberOfPages <= 5}">
					<c:forEach var="i" begin="1" end="${searchPageData.pagination.numberOfPages}" step="1">
						<li><a href="${baseSearchURL}page=${i}" class ="js-page-link${i != searchPageData.pagination.currentPage ? '' : ' active'}" data-page-nr="${i}">${i}</a></li>
					</c:forEach>
				</c:if>

				<%-- display 5 pages with ellipsis in between --%>
				<c:if test="${searchPageData.pagination.numberOfPages > 5}">
					<c:choose>
						<c:when test="${searchPageData.pagination.currentPage <= 3}">
							<c:forEach var="i" begin="1" end="4" step="1">
								<li><a href="${baseSearchURL}page=${i}" class ="js-page-link${i != searchPageData.pagination.currentPage ? '' : ' active'}" data-page-nr="${i}">${i}</a></li>
							</c:forEach>
							<li class="dots">...</li>
							<li><a href="${searchPageData.pagination.lastUrl}" data-page-nr="${searchPageData.pagination.numberOfPages}" class ="js-page-link">${searchPageData.pagination.numberOfPages}</a></li>
						</c:when>

						<c:when test="${searchPageData.pagination.currentPage >= searchPageData.pagination.numberOfPages - 2}">
							<li><a href="${searchPageData.pagination.firstUrl}" data-page-nr="1" class ="js-page-link">1</a></li>
							<li class="dots">...</li>
							<c:forEach var="i" begin="${searchPageData.pagination.numberOfPages - 3}" end="${searchPageData.pagination.numberOfPages}" step="1">
								<li><a href="${baseSearchURL}page=${i}" class ="js-page-link${i != searchPageData.pagination.currentPage ? '' : ' active'}" data-page-nr="${i}">${i}</a></li>
							</c:forEach>
						</c:when>

						<c:otherwise>
							<c:if test="${searchPageData.pagination.numberOfPages > 6}">
								<li><a href="${searchPageData.pagination.firstUrl}" data-page-nr="1" class ="js-page-link">1</a></li>
								<li class="dots">...</li>
								<c:forEach var="i" begin="${searchPageData.pagination.currentPage - 1}" end="${searchPageData.pagination.currentPage + 1}" step="1">
									<li><a href="${baseSearchURL}page=${i}" class ="js-page-link${i != searchPageData.pagination.currentPage ? '' : ' active'}" data-page-nr="${i}">${i}</a></li>
								</c:forEach>
								<li class="dots">...</li>
								<li><a href="${searchPageData.pagination.lastUrl}" data-page-nr="${searchPageData.pagination.numberOfPages}" class ="js-page-link">${searchPageData.pagination.numberOfPages}</a></li>
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:if>
			</ul>

			<%-- show btn-right --%>
			<c:if test="${hasNextPage}">
				<a href="${searchPageData.pagination.nextUrl}" class="btn btn-right js-page-link" data-page-nr="${searchPageData.pagination.currentPage + 1}"><i></i></a>
			</c:if>
		</div>
	</div>
</div>

<%-- doT-Template pagination --%>
<script id="tmpl-pagination" type="text/x-template-dotjs">
	<%-- show btn-left --%>
	{{? it.showPrevButton == "true" }}
	<a href="{{= it.prevUrl}}" class="btn btn-left js-page-link" data-page-nr="{{= it.currentPage - 1}}"><i></i></a>
	{{?}}

	<ul class="pagination">

		<%-- display all pages without ellipsis in between --%>
		{{? it.numberOfPages <= 5}}
		{{~it.pages :item:id}}
		<li><a href="#" class ="js-page-link{{? item.pageNr == it.currentPage}} active{{?}}" data-page-nr="{{= item.pageNr}}">{{= item.pageNr}}</a></li>
		{{~}}
		{{?}}

		<%-- display 5 pages with ellipsis in between --%>
		{{? it.numberOfPages > 5}}
		{{? it.currentPage <= 3 }}
		<%--if--%>
		{{~it.pages :item:id}}
		{{? item.pageNr <= 4}}
		<li><a href="#" class ="js-page-link{{? item.pageNr == it.currentPage}} active{{?}}" data-page-nr="{{= item.pageNr}}">{{= item.pageNr}}</a></li>
		{{?}}
		{{~}}
		<li class="dots">...</li>
		<li><a href="{{= it.lastUrl}}" data-page-nr="{{= it.numberOfPages}}" class ="js-page-link">{{= it.numberOfPages}}</a></li>

		{{?? it.currentPage >= it.numberOfPages - 2 }}
		<%--else if--%>
		<li><a href="{{= it.firstUrl}}" data-page-nr="1" class="js-page-link">1</a></li>
		<li class="dots">...</li>
		{{~it.pages :item:id}}
		{{? item.pageNr >= it.numberOfPages - 3}}
		<li><a href="#" class ="js-page-link{{? item.pageNr == it.currentPage}} active{{?}}" data-page-nr="{{= item.pageNr}}">{{= item.pageNr}}</a></li>
		{{?}}
		{{~}}
		{{??}}
		<%--else--%>
		{{? it.numberOfPages > 6}}
		<li><a href="{{= it.firstUrl}}" data-page-nr="1" class ="js-page-link">1</a></li>
		<li class="dots">...</li>
		{{~it.pages :item:id}}
		{{? item.pageNr >= it.currentPage - 1 && item.pageNr <= it.currentPage + 1}}
		<li><a href="#" class ="js-page-link{{? item.pageNr == it.currentPage}} active{{?}}" data-page-nr="{{= item.pageNr}}">{{= item.pageNr}}</a></li>
		{{?}}
		{{~}}
		<li class="dots">...</li>
		<li><a href="{{= it.lastUrl}}" data-page-nr="{{= it.numberOfPages}}" class ="js-page-link">{{= it.numberOfPages}}</a></li>
		{{?}}
		{{?}}
		{{?}}

	</ul>

	<%-- show btn-right --%>
	{{? it.showNextButton == "true" }}
	<a href="{{= it.nextUrl}}" class="btn btn-right js-page-link" data-page-nr="{{= it.currentPage + 1}}"><i></i></a>
	{{?}}
</script>