<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="row frame">
	<div class="gu-16">
		<div>
			<mod:productlist-pagination template="category" skin="category" />
			<mod:productlist-order />
			<mod:productlist-switch />
		</div>
	</div>
</div>


<div class="ajax-action-overlay">
	<div class="background-overlay"></div>
</div>