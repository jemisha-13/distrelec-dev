<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<c:choose>
	<c:when test="${not empty promoLabel}">
		<c:choose>
			<c:when test="${promoLabel.code eq 'calibrationService' and !currentSalesOrg.calibrationInfoDeactivated}">
				<div class="bd ${promoLabel.code} ">
					<div class="iso">ISO</div> 
					<div class="calibrated">calibrated</div>
				</div>
			</c:when>
			<c:otherwise>
				<c:if test="${promoLabel.code != 'calibrationService'}">
					<span class="bd ${promoLabel.code} ellipsis 2ndOtherwise" title="${promoLabel.label}">
						${promoLabel.label}
					</span>	
				</c:if>		
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${code eq 'cal'}">
				<div class="bd calibrationService">
					<div class="iso">ISO</div> 
					<div class="calibrated">calibrated</div>
				</div>			
			</c:when>
			<c:otherwise>
				<span class="bd ${code} ellipsis 1stOtherwise" title="${label}">
					${label}
				</span>			
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
