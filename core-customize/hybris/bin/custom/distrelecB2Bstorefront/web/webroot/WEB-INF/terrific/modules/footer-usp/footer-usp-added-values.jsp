<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>

<c:set var="guWidth" value="3" />

<div class="">
	<div class="gu-3 padding-left">
<%-- 		<h3 class="value-title"><spring:theme code="footer.addedValues.title1" text="Adding Value to Suppliers" /></h3> --%>
		<div class="row">
			<c:forEach items="${addedValuesList}" var="item" end="5">
				<%@ include file="/WEB-INF/terrific/modules/footer-usp/fragement_usp_item.jsp" %>
			</c:forEach>
		</div>
	</div>
	<div class="gu-6">
<%-- 		<h3 class="value-title"><spring:theme code="footer.addedValues.title2" text="Adding Value to Customers" /><h3> --%>
		<div class="row">
			<c:forEach items="${addedValuesList}" var="item" begin="6" end="11">
				<%@ include file="/WEB-INF/terrific/modules/footer-usp/fragement_usp_item.jsp" %>
			</c:forEach>
		</div>
	</div>
</div>