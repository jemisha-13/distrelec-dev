<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<ul class="address-list">
	<c:forEach var="address" items="${addressList}" varStatus="loop">
		<%-- Only display shipping addresses with a street address --%>
		<c:if test="${addressType ne 'shipping' or (currentCountry.isocode eq 'CH' or not empty address.line1)}">
			<li>
				<c:choose>
					<c:when test="${addressType == 'billing'}">
						<mod:address
							template="${addressType}-${customerType}"
							address="${address}"
							addressActionMode="${addressActionMode}"
							addressEditMode="${addressEditMode}"
							addressType="${addressType}"
							customerType="${customerType}"
							selectedAddressId="${selectedAddressId}"
							skin="billing"
						/>
					</c:when>
					<c:when test="${addressType == 'billing-multiple'}">
						<mod:address
								template="billing-b2b-list"
								address="${address}"
								addressActionMode="${addressActionMode}"
								addressEditMode="${addressEditMode}"
								addressType="${addressType}"
								customerType="${customerType}"
								selectedAddressId="${selectedAddressId}"
								skin="billing"
						/>
					</c:when>
					<c:when test="${addressType == 'shipping'}">
						<mod:address
							template="${addressType}-${customerType}-select"
							skin="address-shipping-select"
							address="${address}"
							addressActionMode="${addressActionMode}"
							addressEditMode="${addressEditMode}"
							addressType="${addressType}"
							customerType="${customerType}"
							addressIndex="${loop.index}"
						/>
					</c:when>
					<c:when test="${addressType == 'pickup'}">
						<mod:address
							template="${addressType}"
							skin="pickup"
							warehouse="${address}"
							addressActionMode="${addressActionMode}"
							addressType="${addressType}"
							customerType="${customerType}"
							selectedAddressId="${selectedAddressId}"
						/>
					</c:when>
				</c:choose>
			</li>
		</c:if>
	</c:forEach>
</ul>
