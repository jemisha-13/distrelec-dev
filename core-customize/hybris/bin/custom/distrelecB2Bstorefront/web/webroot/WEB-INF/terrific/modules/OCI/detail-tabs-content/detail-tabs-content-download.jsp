<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:message code="bomdataimport.download" var="sDownloadText" />
<c:set var="loginUrl" value="/login" />

<div class="loading-downloads">
	<spring:message code="product.gallery.magic360.loading" />
</div>

<div class="download-container">
	<div class="datasheets-container datasheets-container--data  hidden">
		<div class="datasheet-title"><spring:message code="product.downloads.datasheets" /></div>
		<div class="link-download-pdf datasheets-section">
		
		</div>
	</div>

	<div class="datasheets-container datasheets-container--manuals hidden">
		<div class="manual-title"><spring:message code="product.downloads.manuals" /></div>
		<div class="link-download-pdf manuals-section">
		
		</div>
	</div>
	
	<div class="datasheets-container datasheets-container--brochures hidden">
		<div class="brochure-title"><spring:message code="product.downloads.brochures" /></div>
		<div class="link-download-pdf brochure-section">
		
		</div>
	</div>
	
	<div class="datasheets-container datasheets-container--certificates hidden">
		<div class="certificate-title"><spring:message code="product.downloads.certificates" /></div>
		<div class="link-download-pdf certificate-section">
		
		</div>
	</div>
	
	<div class="datasheets-container datasheets-container--software hidden">
		<div class="software-title"><spring:message code="product.downloads.software" /></div>
		<div class="link-download-pdf software-section">
		
		</div>
	</div>
	
	<div class="datasheets-container datasheets-container--templates hidden">
		<div class="template-title"><spring:message code="product.downloads.templates" /></div>
		<div class="link-download-pdf template-section">
		
		</div>
	</div>		
	
</div>
