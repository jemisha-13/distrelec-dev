<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="row">
	<div class="gu-12">
		<div class="frame">
			<div class="switch-instructions">
				<spring:message code='productlistswitch.category.instructions' />
			</div>
			<mod:productlist-switch skin="category category-main" template="category" />
		</div>
	</div>
</div>