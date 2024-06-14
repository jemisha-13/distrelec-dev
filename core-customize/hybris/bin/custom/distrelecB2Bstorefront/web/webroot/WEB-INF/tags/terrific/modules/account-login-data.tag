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
<%@ tag description="Module: account-login-data - Templates: default" %>
<%@ attribute name="isMigratedUser" description="Is the current user a new user or a migrated user?" type="java.lang.Boolean" rtexprvalue="true" %>
<%@ attribute name="currentEmail" description="Current Email address" type="java.lang.String" rtexprvalue="true" %>

<%-- Module template selection --%>
<terrific:mod name="account-login-data" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${template == 'allowed-permissions'}">
			<%@ include file="/WEB-INF/terrific/modules/account-login-data/account-login-data-allowed-permissions.jsp" %>
		</c:when>
		<c:when test="${template == 'approved-budget'}">
			<%@ include file="/WEB-INF/terrific/modules/account-login-data/account-login-data-approved-budget.jsp" %>
		</c:when>
		<c:when test="${template == 'change-email'}">
			<%@ include file="/WEB-INF/terrific/modules/account-login-data/account-login-data-change-email.jsp" %>
		</c:when>
		<c:when test="${template == 'change-name'}">
			<%@ include file="/WEB-INF/terrific/modules/account-login-data/account-login-data-change-name.jsp" %>
		</c:when>
		<c:when test="${template == 'change-password'}">
			<%@ include file="/WEB-INF/terrific/modules/account-login-data/account-login-data-change-password.jsp" %>
		</c:when>
		<c:when test="${template == 'newsletter'}">
			<%@ include file="/WEB-INF/terrific/modules/account-login-data/account-login-data-newsletter.jsp" %>
		</c:when>
		<c:when test="${template == 'obsole-notify'}">
			<%@ include file="/WEB-INF/terrific/modules/account-login-data/account-login-data-obsole-notify.jsp" %>
		</c:when>
		<c:when test="${template == 'preferences'}">
			<%@ include file="/WEB-INF/terrific/modules/account-login-data/account-login-data-preferences.jsp" %>
		</c:when>
		<c:when test="${template == 'your-account'}">
			<%@ include file="/WEB-INF/terrific/modules/account-login-data/account-login-data-your-account.jsp" %>
		</c:when>
		<c:when test="${template == 'success-newsletter'}">
			<%@ include file="/WEB-INF/terrific/modules/account-login-data/account-login-success-newsletter.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="/WEB-INF/terrific/modules/account-login-data/account-login-data.jsp" %>
		</c:otherwise>
	</c:choose>
</terrific:mod>
