<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<div class="box box-address box-address-${address.id} address-shipping-b2c">
	<input type="text" class="hidden address-id" value="${address.id}" />
	<div class="row">
		<div class="col-12 box-address__preview">
			<p>
				<c:out value="${address.title}" />
				<c:out value=" ${address.firstName}" />
				<c:out value=" ${address.lastName}" />
			</p>
			<c:if test="${not empty address.line1}">
				<p>
					<c:out value="${address.line1}" />
					<c:out value=" ${address.line2}" />
				</p>
			</c:if>
			<c:if test="${not empty address.postalCode}">
				<p>
					<c:out value="${address.postalCode}" />
					<c:out value=" ${address.town}" />
				</p>
			</c:if>
			<c:if test="${not empty address.country.name}">
				<p>
					<c:out value="${address.country.name}" />
				</p>
			</c:if>
		</div>
	</div>
</div>

