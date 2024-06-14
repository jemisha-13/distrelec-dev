<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:theme code="shoppinglist.list.addToCart" text="Add list to Cart" var="addListToCart" />
<spring:theme code="shoppinglist.list.addToCart.selectedItems" text="Add selected items to cart" var="addSelectedItemsToCart" />

<button class="btn btn-primary btn-cart fb-add-to-cart disabled" data-list-id="${listId}">
	<i></i>
	<span class="all-items active">${addListToCart}</span>
	<span class="selected-items">${addSelectedItemsToCart}</span>
</button>


