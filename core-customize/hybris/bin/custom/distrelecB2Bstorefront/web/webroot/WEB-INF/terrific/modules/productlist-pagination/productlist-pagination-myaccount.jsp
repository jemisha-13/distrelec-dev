<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- determine pageSize since BE is not able to store it --%>
<c:set var="resultCount" value="${fn:length(myAccountSearchPageData.results)}" />

<c:choose>
	<c:when test="${resultCount > 25 && resultCount <= 50}">
		<c:set var="pageSize" value="50" />
	</c:when>
	<c:when test="${resultCount > 10 && resultCount <= 25}">
		<c:set var="pageSize" value="25" />
	</c:when>
	<c:otherwise>
		<c:set var="pageSize" value="10" />
	</c:otherwise>
</c:choose>

<%-- currentPage from BE is zero-based, so we have to add 1 to get the real currentPage --%>
<c:set var="currentPage" value="${myAccountSearchPageData.pagination.currentPage + 1}" />

<div class="row">
	<div class="col-12 col-md-6">
		<c:choose>
			<c:when test="${htmlClasses eq 'quote'}">
				<label for="productlist-pagination"><spring:message code="product.list.show.quotesPerPage" /></label>
			</c:when>
			<c:otherwise>
				<label for="productlist-pagination"><spring:message code="product.list.show.perPage" /></label>
			</c:otherwise>
		</c:choose>
		<select id="select-productlist-pagination" name="productlist-pagination" class="selectpicker selectboxit-meta js-page-size" id="productlist-pagination">
			<option value="10" selected="selected">10</option>
			<option value="25" ${pageSize == 25 ? 'selected="selected"':''}>25</option>
			<option value="50" ${pageSize == 50 ? 'selected="selected"':''}>50</option>
		</select>
	</div>
	<div class="col-12 col-md-6">
		<div class="pagination-wrapper">

			<c:set var="hasPreviousPage" value="${currentPage > 1}" />
			<c:set var="hasNextPage" value="${(currentPage + 1) <= myAccountSearchPageData.pagination.numberOfPages}" />

			<%-- show btn-left --%>
			<c:if test="${hasPreviousPage}">
				<a href="${searchmyAccountSearchPageData.pagination.prevUrl}" class="btn btn-left js-page-link" data-page-nr="${currentPage - 1}"><i></i></a>
			</c:if>

			<ul class="pagination">

				<%-- display all pages without ellipsis in between --%>
				<c:if test="${myAccountSearchPageData.pagination.numberOfPages <= 5}">
					<c:forEach var="i" begin="1" end="${myAccountSearchPageData.pagination.numberOfPages}" step="1">
						<li><a href="${baseSearchURL}&amp;page=${i}" class ="js-page-link${i != currentPage ? '' : ' active'}" data-page-nr="${i}">${i}</a></li>
					</c:forEach>
				</c:if>

				<%-- display 5 pages with ellipsis in between --%>
				<c:if test="${myAccountSearchPageData.pagination.numberOfPages > 5}">
					<c:choose>
						<c:when test="${currentPage <= 3}">
							<c:forEach var="i" begin="1" end="4" step="1">
								<li><a href="${baseSearchURL}&amp;page=${i}" class ="js-page-link${i != currentPage ? '' : ' active'}" data-page-nr="${i}">${i}</a></li>
							</c:forEach>
							<li class="dots">...</li>
							<li><a href="${myAccountSearchPageData.pagination.lastUrl}" data-page-nr="${myAccountSearchPageData.pagination.numberOfPages}" class ="js-page-link">${myAccountSearchPageData.pagination.numberOfPages}</a></li>
						</c:when>

						<c:when test="${currentPage >= myAccountSearchPageData.pagination.numberOfPages - 2}">
							<li><a href="${myAccountSearchPageData.pagination.firstUrl}" data-page-nr="1" class ="js-page-link">1</a></li>
							<li class="dots">...</li>
							<c:forEach var="i" begin="${myAccountSearchPageData.pagination.numberOfPages - 3}" end="${myAccountSearchPageData.pagination.numberOfPages}" step="1">
								<li><a href="${baseSearchURL}&amp;page=${i}" class ="js-page-link${i != currentPage ? '' : ' active'}" data-page-nr="${i}">${i}</a></li>
							</c:forEach>
						</c:when>

						<c:otherwise>
							<c:if test="${myAccountSearchPageData.pagination.numberOfPages > 6}">
								<li><a href="${myAccountSearchPageData.pagination.firstUrl}" data-page-nr="1" class ="js-page-link">1</a></li>
								<li class="dots">...</li>
								<c:forEach var="i" begin="${currentPage - 1}" end="${currentPage + 1}" step="1">
									<li><a href="${baseSearchURL}&amp;page=${i}" class ="js-page-link${i != currentPage ? '' : ' active'}" data-page-nr="${i}">${i}</a></li>
								</c:forEach>
								<li class="dots">...</li>
								<li><a href="${myAccountSearchPageData.pagination.lastUrl}" data-page-nr="${myAccountSearchPageData.pagination.numberOfPages}" class ="js-page-link">${myAccountSearchPageData.pagination.numberOfPages}</a></li>
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:if>
			</ul>

			<%-- show btn-right --%>
			<c:if test="${hasNextPage}">
				<a href="${myAccountSearchPageData.pagination.nextUrl}" class="btn btn-right js-page-link" data-page-nr="${currentPage + 1}"><i></i></a>
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
				<li><a href="#" class ="js-page-link{{? item.pageNr == it.currentPage+1}} active{{?}}" data-page-nr="{{= item.pageNr}}">{{= item.pageNr}}</a></li>
			{{~}}
		{{?}}

		<%-- display 5 pages with ellipsis in between --%>
		{{? it.numberOfPages > 5}}
			{{? it.currentPage <= 3 }}
				<%--if--%>
				{{~it.pages :item:id}}
					{{? item.pageNr <= 4}}
						<li><a href="#" class ="js-page-link{{? item.pageNr == it.currentPage+1}} active{{?}}" data-page-nr="{{= item.pageNr}}">{{= item.pageNr}}</a></li>
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
						<li><a href="#" class ="js-page-link{{? item.pageNr == it.currentPage+1}} active{{?}}" data-page-nr="{{= item.pageNr}}">{{= item.pageNr}}</a></li>
					{{?}}
				{{~}}
			{{??}}
				<%--else--%>
				{{? it.numberOfPages > 6}}
					<li><a href="{{= it.firstUrl}}" data-page-nr="1" class ="js-page-link">1</a></li>
					<li class="dots">...</li>
					{{~it.pages :item:id}}
						{{? item.pageNr >= it.currentPage - 1 && item.pageNr <= it.currentPage + 1}}
							<li><a href="#" class ="js-page-link{{? item.pageNr == it.currentPage+1}} active{{?}}" data-page-nr="{{= item.pageNr}}">{{= item.pageNr}}</a></li>
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
		<a href="{{= it.nextUrl}}" class="btn btn-right js-page-link" data-page-nr="{{= it.currentPage + 2}}"><i></i></a>
	{{?}}
</script>