<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>

<c:set var="guWidth" value="2" />

<div class="row">
	<c:forEach items="${USPsList}" var="item">
		<%@ include file="/WEB-INF/terrific/modules/footer-usp/fragement_usp_item.jsp" %>
	</c:forEach>
</div>