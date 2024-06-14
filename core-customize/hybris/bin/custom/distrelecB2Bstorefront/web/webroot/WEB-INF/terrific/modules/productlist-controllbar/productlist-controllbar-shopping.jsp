<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="frame">
	<mod:shoppinglist-select-all disabledState="${productsInListCount == 0 ? true : false }"/>
	<mod:productlist-order template="shopping" skin="shopping" productListOrders="${productListOrders}" disabledState="${productsInListCount == 0 ? true : false }" />
</div>