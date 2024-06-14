<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>


<json:object>
	<json:property name="status" value="${status}" />
	<json:property name="message">
		<spring:message code="${message}" text="" />
	</json:property>
</json:object>

