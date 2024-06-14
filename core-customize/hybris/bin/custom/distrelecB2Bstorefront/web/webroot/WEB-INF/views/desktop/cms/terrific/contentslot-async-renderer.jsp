<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>


<c:if test="${not empty asyncContentSlot}">
	<cms:slot var="comp" contentSlot="${asyncContentSlot}">
		<cms:component component="${comp}" />
	</cms:slot>			
</c:if>
