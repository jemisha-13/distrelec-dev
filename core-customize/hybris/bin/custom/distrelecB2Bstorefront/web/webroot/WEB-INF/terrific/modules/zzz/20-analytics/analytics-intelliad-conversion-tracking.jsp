<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- Format Shipping Costs Value --%>
<c:choose>
	<c:when test="${empty order.subTotal.value}">
		<c:set var="totalCartValue" value="" />
	</c:when>
	<c:otherwise>
		<c:set var="totalCartValue" value="${namicscommerce:formatAnalyticsPrice(order.subTotal)}" />
	</c:otherwise>
</c:choose>

<%-- Create Product Codes string --%>
<c:set var="productCodes" value="" />
<c:forEach items="${order.entries}" var="orderEntry" varStatus="status" >
	<c:set var="productCodes" value="${status.first ? '' : productCodes}${not status.first ? '|' : ''}${orderEntry.product.codeErpRelevant}" />
</c:forEach>


<script language="JavaScript" type="text/javascript">
	var ia_tp = "t23.intelliad.de/tc2.js";
	var ia_v = "${totalCartValue}";
	var ia_vz = "sa";
	var ia_vv = "${order.totalPrice.currencyIso}";
	var ia_po = "${order.code}";
	var ia_c1 = "";
	var ia_c2 = "";
	var ia_c3 = "";
	var ia_c4 = "";
	var ia_pi = "${productCodes}";

	var ia_cl = "${intelliAdTrackingId}";
	var ia_rand = Math.floor(Math.random() * 11111139435231);

	var ia_link = ia_tp + '?cl=' + ia_cl + '&v=' + ia_v + '&vz=' + ia_vz
			+ '&vv=' + ia_vv + '&po=' + ia_po + '&c1=' + ia_c1 + '&c2=' + ia_c2
			+ '&c3=' + ia_c3 + '&c4=' + ia_c4 + '&pi=' + ia_pi + '&rand='
			+ ia_rand;

	document.write('<script src="http'
			+ (document.location.protocol == 'https:' ? 's' : '') + '://'
			+ ia_link + '"></sc'+'ript>');
</script>

<noscript>
	<img src="//t23.intelliad.de/tc.php?cl=${intelliAdTrackingId}&v=${totalCartValue}&vz=sa&vv=${order.totalPrice.currencyIso}&po=${order.code}&c1=&c2=&c3=&c4=&pi=${productCodes}" width="1" height="1" alt="intelliAd" />
</noscript>




