<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:theme code="shoppinglist.shopping.noProducts" text="No Products in this list" var="bomListIsEmpty" />

<c:set var="productsPerPage" value="10" />
<c:set var="pageSize" value="${productsPerPage}" />

<div class="productlist">
	<ul class="list">
		<c:forEach items="${matchingProducts}" var="matchingProduct" varStatus="status" >
			<li class="list__item">
				<mod:product tag="div" matchingProduct="${matchingProduct}" template="bom" skin="bom ${(not empty matchingProduct.product.endOfLifeDate or not matchingProduct.product.buyable) ? ' skin-product-not-buyable' : '' }" position="${status.index + 1}"/>
			</li>
		</c:forEach>
	</ul>

</div>