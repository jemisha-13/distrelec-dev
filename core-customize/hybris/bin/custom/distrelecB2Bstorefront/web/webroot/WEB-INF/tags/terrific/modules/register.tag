<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>

<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ tag description="Module: Register - Templates: default" %>
<%@ attribute name="b2cAction" description="Register as Business to Customer user" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="b2bAction" description="Register as Business to Business user" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="existingB2CAction" description="Register as Business to Customer user" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="existingB2BAction" description="Register as Business to Business user" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="existingCustomerAction" description="Register as an existing Customer" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="guestAction" description="Register as Guest to Customer user" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="action" %>
<%@ attribute name="captcha" type="java.lang.String" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<terrific:mod name="register" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template == 'success'}">
					<%@ include file="/WEB-INF/terrific/modules/register/register-success.jsp" %>
				</c:when>
				<c:when test="${template == 'homepage'}">
					<%@ include file="/WEB-INF/terrific/modules/register/register-homepage.jsp" %>
				</c:when>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${template == 'success'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/register/register-success.jsp" %>
				</c:when>
				<c:when test="${template == 'homepage'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/register/register-homepage.jsp" %>
				</c:when>
			</c:choose>
		</c:otherwise>
	</c:choose>
</terrific:mod>
