<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Specific module settings --%>
<%@ tag description="Module: login - Templates: default" %>
<%@ attribute name="b2cAction" description="Register as Business to Customer user" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="b2bAction" description="Register as Business to Business user" type="java.lang.String" rtexprvalue="true" %>

<%-- Module template selection --%>
<terrific:mod name="login" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${template == 'resend-password'}">
			<%@ include file="/WEB-INF/terrific/modules/login/resend-password.jsp" %>
		</c:when>
		<c:when test="${template == 'change-password'}">
			<%@ include file="/WEB-INF/terrific/modules/login/change-password.jsp" %>
		</c:when>
		<c:when test="${template == 'setinitial-password'}">
			<%@ include file="/WEB-INF/terrific/modules/login/setinitial-password.jsp" %>
		</c:when>
		<c:when test="${template == 'resend-activation-token'}">
			<%@ include file="/WEB-INF/terrific/modules/login/resend-activation-token.jsp" %>
		</c:when>
		<c:when test="${template == 'checkout'}">
			<%@ include file="/WEB-INF/terrific/modules/login/checkout.jsp" %>
		</c:when>
		<c:when test="${template == 'checkout-forgotten-password'}">
			<%@ include file="/WEB-INF/terrific/modules/login/checkout-forgotten-password.jsp" %>
		</c:when>
		<c:when test="${template == 'checkout-forgotten-password-success'}">
			<%@ include file="/WEB-INF/terrific/modules/login/checkout-forgotten-password-success.jsp" %>
		</c:when>
		<c:when test="${template == 'newcheckout'}">
			<%@ include file="/WEB-INF/terrific/modules/login/login-newcheckout.jsp" %>
		</c:when>
		<c:when test="${template == 'guest-checkout'}">
			<%@ include file="/WEB-INF/terrific/modules/login/login-guest-checkout.jsp" %>
		</c:when>
		<c:when test="${template == 'new-customer-info'}">
			<%@ include file="/WEB-INF/terrific/modules/login/login-new-customer-info.jsp" %>
		</c:when>
    <c:otherwise>
        <%@ include file="/WEB-INF/terrific/modules/login/login.jsp" %>
    </c:otherwise>
	</c:choose>
</terrific:mod>
