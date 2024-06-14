<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>

<json:object>
	<json:property name="error" value="${errors}" />
	<c:if test="${not empty fieldErrors}">
		<json:object name="fields">
			<c:forEach items="${fieldErrors}" var="fieldError">
				<json:property name="${fieldError.field}" value="${fieldError.defaultMessage}" />
			</c:forEach>
		</json:object>
	</c:if>
</json:object>
