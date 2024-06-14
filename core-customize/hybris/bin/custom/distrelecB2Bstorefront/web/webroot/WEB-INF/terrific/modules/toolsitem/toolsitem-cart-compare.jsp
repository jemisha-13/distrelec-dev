<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:message code='toolsitem.add.cart.compare' var="sCartCompare"/>
<a class="btn-cart fb-add-to-cart" title="${sCartTitle}"
   data-aainteraction="add to cart"
   data-product-code="${productId}">${sCartCompare}</a>