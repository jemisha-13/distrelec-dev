<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>


<%-- Format Total Order Price Value --%>
<c:choose>
	<c:when test="${empty order.subTotal.value}">
		<c:set var="totalCartValue" value="" />
	</c:when>
	<c:otherwise>
		<c:set var="totalCartValue" value="${namicscommerce:formatAnalyticsPrice(order.subTotal)}" />
	</c:otherwise>
</c:choose>

<!-- Google Code for Sale Conversion Page -->
<script type="text/javascript">
	/* <![CDATA[ */
	var google_conversion_id = ${googleAdWordsConversionTrackingId};
	var google_conversion_language = "en";
	var google_conversion_format = "3";
	var google_conversion_color = "ffffff";
	var google_conversion_label = "${googleAdWordsConversionLabel}";
	var google_conversion_value = "${totalCartValue}";
	var google_remarketing_only = false;
	/* ]]> */
</script>

<script type="text/javascript" src="//www.googleadservices.com/pagead/conversion.js"></script>

<noscript>
	<div style="display: inline;">
		<img height="1" width="1" style="border-style: none;" alt="" src="//www.googleadservices.com/pagead/conversion/${googleAdWordsConversionTrackingId}/?value=${totalCartValue}&amp;label=${googleAdWordsConversionLabel}&amp;guid=ON&amp;script=0" />
	</div>
</noscript>




