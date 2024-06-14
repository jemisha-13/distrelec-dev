<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:theme code="shoppinglist.shopping.noProducts" text="No Products in this list" var="shoppingListIsEmpty" />

<c:set var="productsPerPage" value="10" />
<c:set var="pageSize" value="${productsPerPage}" />

<div class="productlist" data-track-link-id="${wtTeaserTrackingId}.add-ltc.-">
    <ul class="list">

        <c:forEach items="${currentList.entries}" var="wishlistEntry" varStatus="status" >
            <mod:product tag="li" template="shopping" skin="shopping${(not empty wishlistEntry.product.endOfLifeDate or not wishlistEntry.product.buyable) ? ' skin-product-not-buyable' : '' }" position="${status.index + 1}" htmlClasses="${status.count > pageSize ? 'paged' : ''}" shoppinglistId="${currentList.uniqueId}" wishlistEntry="${wishlistEntry}" productCounter="${status.count}" />
        </c:forEach>
    </ul>
</div>

<c:if test="${fn:length(currentList.entries) < 1}">
    <p class="empty-list${currentList.totalUnitCount == 0 ? ' active' : '' }">${shoppingListIsEmpty}</p>
</c:if>

<c:if test="${currentList.totalUnitCount > productsPerPage}">
	<div class="row row-show-more">
	    <a class="btn btn-down btn-show-more" href="#" data-page-size="${pageSize}"><spring:message code="product.list.show.more" /><i></i></a>
	</div>
</c:if>