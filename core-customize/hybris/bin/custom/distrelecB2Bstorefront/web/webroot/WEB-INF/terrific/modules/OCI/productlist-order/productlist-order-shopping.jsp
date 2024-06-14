<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="currentOrderCode" value="DateAddedDesc"/>
<c:if test="${not empty currentOrder}">
	<c:set var="currentOrderCode" value="${currentOrder.code}"/>
</c:if>

<select id="select-productlist-order" name="productlist-order" class="selectpicker selectboxit-meta" ${disabledState ? 'disabled="disabled"' : ''} data-pretext='<spring:message code="product.order.by" />: '>
	<c:forEach items="${productListOrders}" var="sortItem">
		<option value="${sortItem.code}" ${sortItem.code == currentOrderCode ? 'selected' : ''}><spring:message code="shoppinglist.sort.${sortItem.code}" /></option>
	</c:forEach>
</select>