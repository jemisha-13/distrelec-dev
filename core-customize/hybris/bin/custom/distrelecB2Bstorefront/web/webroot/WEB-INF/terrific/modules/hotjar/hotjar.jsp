<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%-- DISTRELEC-9346 Hotjar tracking issue /checkout/orderConfirmation --%>
<c:if test="${wtPageId eq 'orderConfirmationPage' or wtPageId eq 'orderConfirmationPickupPage' or wtPageId eq 'orderConfirmationApprovalPage' }">
<script>
	//hotjar js snippet to integrate on /orderConfirmation/ page below snippet.
	if (window.location.href.match(/.*distrelec.ch.*/)) {
		(function(h, o, t, j, a, r) {
			h.hj = h.hj || function() {
				(h.hj.q = h.hj.q || []).push(arguments)
			};
			h._hjSettings = {
				hjid: 180942,
				hjsv: 5
			};
			a = o.getElementsByTagName('head')[0];
			r = o.createElement('script');
			r.async = 1;
			r.src = t + h._hjSettings.hjid + j + h._hjSettings.hjsv;
			a.appendChild(r);
		})(window, document, '//static.hotjar.com/c/hotjar-', '.js?sv=');
	}
</script>
</c:if>