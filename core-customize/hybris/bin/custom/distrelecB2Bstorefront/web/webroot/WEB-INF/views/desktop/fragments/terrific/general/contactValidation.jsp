<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<json:object>
	<json:property name="status" value="${status}"/>
	<json:property name="suggestedNumber" value="${suggestedNumber}"/>
	<json:property name="message" value="${message}"/>
</json:object>