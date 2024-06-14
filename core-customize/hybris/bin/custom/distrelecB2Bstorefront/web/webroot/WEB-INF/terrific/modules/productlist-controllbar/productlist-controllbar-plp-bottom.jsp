<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:theme code="product.list.controllBar.autoApplyFilters" text="Auto apply filters" var="sAutoApplyFilters" />

<mod:productlist-pagination template="plp" skin="plp" htmlClasses="plp-pagination-bottom" />
<mod:productlist-order  template="plp" skin="plp"/>
<mod:productlist-order  template="plp-mobile" skin="plp-mobile"/>

<div class="ajax-action-overlay">
	<div class="background-overlay"></div>
</div>