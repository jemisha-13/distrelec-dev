<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<title><spring:message code="productinfo.title" /></title>
</head>
<body>
	<spring:message code="productinfo.articlenumber" />: ${articleNumber}<br /> 
	<spring:message code="productinfo.stock" />: ${stock} <br /> 
	<spring:message code="productinfo.availability" />: ${deliveryTime}
</body>
</html>