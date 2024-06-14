<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>

<json:object>
	<json:object name="order">
		<json:property name="attribute" value="${attribute}" />
	</json:object>
</json:object>
