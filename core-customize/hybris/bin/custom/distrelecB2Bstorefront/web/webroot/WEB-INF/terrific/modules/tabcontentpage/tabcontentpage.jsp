<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>



<div class="container-more-information">
	<ul class="nav nav-tabs info-tabs">
		<c:forEach var="slot" items="${slots}">
			<c:if test="${slot.key ne 'ViewAllProducts'  &&  not empty slot.value.title  }">
				<li id="${slot.value.uid}">
					<a href="${slot.key}" data-toggle="tab" data-uid="${slot.value.uid}" class="ellipsis" title="${slot.value.title}"> ${slot.value.title}  </a>
				</li>
			</c:if>
		</c:forEach>

	</ul>
	
	
	<c:if test="${not empty slots['ViewAllProducts']}">
		<ul class="nav nav-tabs info-tabs right-tab">
			<c:set var="slot" value="${slots['ViewAllProducts']}" />
			<li class="viewAllProducts">
				<a href="${slot.cmsComponents[0].localizedUrl}" data-toggle="tab" class="viewAllProducts" title="${slot.cmsComponents[0].linkName}"> 
					<i></i> 
					<p class="ellipsis">
						${slot.cmsComponents[0].linkName} 
					</p>
				</a>
			</li>
		</ul>
	</c:if>
	
	
	<div class="content-container">
	
		<c:set var="found" value="false" />
	
		<c:forEach var="slot" items="${slots}" varStatus="status" >
			
			<c:if test="${slot.key ne 'ViewAllProducts' &&  not empty slot.value.title && found ne 'true' }">
			
				<c:set var="found" value="true" />
			
				<div class="tab-content uid-${slot.value.uid}">
					<cms:slot var="feature" contentSlot="${slot.value}"> 
			 			<cms:component component="${feature}" /> 
			 		</cms:slot> 
		 		</div>	
	 		</c:if>
		</c:forEach>
	
	</div>
	
</div>


