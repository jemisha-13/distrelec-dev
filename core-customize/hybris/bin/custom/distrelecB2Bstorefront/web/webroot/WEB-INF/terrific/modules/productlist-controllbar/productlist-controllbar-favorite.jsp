<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="frame">
	<mod:productlist-order template="favorite" skin="favorite" productListOrders="${productListOrders}" disabledState="${productsInListCount == 0 ? true : false }" />
	<%-- Module is not active for Phase 1 --%>
	<%--<mod:productlist-switch />--%>
</div>