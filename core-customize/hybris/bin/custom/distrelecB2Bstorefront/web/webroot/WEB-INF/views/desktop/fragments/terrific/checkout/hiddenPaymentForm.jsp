<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<form name="hiddenPaymentForm" method="POST" action="${hostedOrderPageData.postUrl}" target="${hostedOrderPageData.parameters.Target}">
	<c:forEach items="${hostedOrderPageData.parameters}" var="entry">
		<input type="hidden" name="${entry.key}" value="${entry.value}" />
	</c:forEach>
</form>

<script type="text/javascript">document.forms["hiddenPaymentForm"].submit();</script>

