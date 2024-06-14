<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:theme code="product.list.controllBar.autoApplyFilters" text="Auto apply filters" var="sAutoApplyFilters" />

<div class="row frame">
	<div class="gu-12">
		<div>
			<mod:productlist-pagination template="category" skin="category" />
			<mod:productlist-order />
			<mod:productlist-switch template="category" skin="category" />
		</div>
	</div>
</div>


<div class="ajax-action-overlay">
	<div class="background-overlay"></div>
</div>
