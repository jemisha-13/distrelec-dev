<%@ page trimDirectiveWhitespaces="true" contentType="text/html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html>
<head>
	<script type="text/javascript">
		parent.document.location.href='/cart';
	</script>
</head>
<body style="background-color: #FFFFFF; background-image: none;">
	<c:if test="${ not empty errorMsg}">
		<p><spring:theme code="${errorMsg}" text="Unknown error occurs during the request processing" /></p>
	</c:if>
</body>
</html>
