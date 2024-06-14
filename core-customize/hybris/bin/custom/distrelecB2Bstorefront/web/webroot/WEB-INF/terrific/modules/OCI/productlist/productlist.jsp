<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- ${useTechnicalView} comes from backend from the session --%>
<%-- Product has always standard view if not buyable --%>

<c:forEach items="${searchPageData.pagination.productsPerPageOptions}" var="productsPerPageOption">
	<c:if test="${productsPerPageOption.selected}">
		<c:set var="pageSize" value="${productsPerPageOption.value}" />
	</c:if>
</c:forEach>

<div class="productlist" data-current-query-url="${searchPageData.currentQuery.url}" data-page="${searchPageData.pagination.currentPage}" data-page-size="${pageSize}">
    <ul class="list">
        <c:forEach items="${searchPageData.results}" var="product" varStatus="status">
        	<c:set var="productNotBuyable" value="${(not empty product.endOfLifeDate or not product.buyable) ? true : 'false'}"/>
        	<%-- because the terrific tag library only transforms first argument to a correct skin class, we need this complicated logic --%>
        	<c:set var="productSkin" >
	        	<c:choose>
	        		<c:when test="${(not empty product.endOfLifeDate or not product.buyable)}">
	        			not-buyable
	        		</c:when>
	        		<c:when test="${useTechnicalView}">
	        			technical
	        		</c:when>
	        	</c:choose>
	        </c:set>
            <mod:product tag="li" product="${product}" 
            	template="${not productNotBuyable and useTechnicalView ? 'technical' : ''}" 
            	listViewType="${not productNotBuyable and useTechnicalView ? 'technical' : ''}" 
            	skin="${productSkin}" 
            	position="${status.index + 1}"
            />
        </c:forEach>
        <%-- we always need the product templates since they are also used for the list view switch between standard and technical view --%>
		<mod:product tag="li" template="dot-tpl" skin="template" />
		<mod:product tag="li" template="technical-dot-tpl" skin="template" />
    </ul>
</div>

<mod:loading-state skin="loading-state" />

<div class="ajax-product-loader js-apply-facets">
	<div class="background-overlay apply-facets"></div>
	<div class="message-wrapper js-apply-facets">
		<p class="loading-message apply-facets">
			<a href="" class="js-update-result" data-current-query-url="${searchPageData.currentQuery.url}"><spring:message code="product.list.reloadLayer" /></a>
		</p>
	</div>
</div>
