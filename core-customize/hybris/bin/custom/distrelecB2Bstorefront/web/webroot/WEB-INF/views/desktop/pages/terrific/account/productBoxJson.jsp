<%@ page trimDirectiveWhitespaces="true" pageEncoding="UTF-8" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<json:object>
	<json:property name="status" value="${status}" />
	<json:property name="message" value="${message}" />
</json:object>
