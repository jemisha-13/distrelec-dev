<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="false"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<%-- there will be either the object carpetData set or the three objects columnXItems --%>
<mod:carpet  
	title="${component.title}"  
	carpetData="${carpetData}"
	column1Items="${column1Items}"
	column2Items="${column2Items}"
	column3Items="${column3Items}"
	wtTeaserTrackingId="${wtTeaserTrackingId}"
/>
