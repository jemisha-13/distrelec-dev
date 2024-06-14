<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:theme code="shoppinglist.favorite.noProducts" text="No Products in this list" var="favoriteListIsEmpty" />

<c:set var="productsPerPage" value="10" />
<c:set var="pageSize" value="${productsPerPage}" />

<div class="productlist">
    <ul class="list row">
        <c:forEach items="${currentList.entries}" var="wishlistEntry" varStatus="status" >
            <mod:product template="favorite" tag="li" skin="favorite${(not empty wishlistEntry.product.endOfLifeDate or not wishlistEntry.product.buyable) ? ' skin-product-not-buyable' : '' }" htmlClasses="${status.count > pageSize ? 'paged' : ''}" attributes="id='${wishlistEntry.product.code}'" position="${status.index + 1}" product="${wishlistEntry.product}"/>
        </c:forEach>
    </ul>
</div>
<p class="empty-list${currentList.totalUnitCount == 0 ? ' active' : '' }">${favoriteListIsEmpty}</p>
<c:if test="${currentList.totalUnitCount > productsPerPage}">
	<div class="row row-show-more">
	    <a class="btn btn-down btn-show-more" href="#"data-page-size="${pageSize}" ><spring:message code="product.list.show.more" /><i></i></a>
	</div>
</c:if>