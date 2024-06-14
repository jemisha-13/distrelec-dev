<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
	<c:when test="${not empty promoLabel}">
		<c:choose>
			<c:when test="${promoLabel.code eq 'calibrationService'}">
				<div class="bd ${promoLabel.code}">
					<div class="iso">ISO</div> 
					<div class="calibrated">calibrated</div>
				</div>	
			</c:when>
			<c:otherwise>
				<span class="bd ${promoLabel.code} ellipsis" title="${promoLabel.label}">
					${promoLabel.label}
				</span>			
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<span class="bd ${code} ellipsis" title="${label}">
			${label}
		</span>
	</c:otherwise>
</c:choose>
