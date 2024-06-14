<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<%-- Format Total Order Price Value --%>
<c:choose>
	<c:when test="${empty order.subTotal.value}">
		<c:set var="totalCartValue" value="" />
	</c:when>
	<c:otherwise>
		<c:set var="totalCartValue" value="${namicscommerce:formatAnalyticsPrice(order.subTotal)}" />
	</c:otherwise>
</c:choose>

<img src="https://partners.webmasterplan.com/registersale.asp?site=12562&mode=pps&ltype=1&price=${totalCartValue}&order=${order.code}" width="1" height="1" />

<script type="text/javascript" id="affilinet_advc">
	var type = "Checkout";
	var site = "${affilinetConversionTrackingSiteId}";
</script>
<script type="text/javascript" src="https://partners.webmasterplan.com/art/JS/param.aspx"></script>
