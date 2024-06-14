<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<ul class="tools-bar tools-bar__product-compare" data-product-id="${productId}" >
	<mod:toolsitem template="toolsitem-cart-compare" skin="cart" tag="li" productId="${productId}"/>
	<mod:toolsitem template="toolsitem-shopping-compare" skin="shopping" tag="li" productId="${productId}"/>
</ul>
