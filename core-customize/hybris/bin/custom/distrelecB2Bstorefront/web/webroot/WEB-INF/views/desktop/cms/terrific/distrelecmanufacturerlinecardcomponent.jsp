<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:if test="${not empty linecardItems}">
	<mod:manufacturer-linecard />
</c:if>


