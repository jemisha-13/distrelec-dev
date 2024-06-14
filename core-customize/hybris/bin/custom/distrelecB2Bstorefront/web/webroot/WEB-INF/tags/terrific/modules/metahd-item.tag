<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Module template selection --%>
<terrific:mod name="metahd-item" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<%-- set isOCI var --%>
	<c:set var="isOCI" value="false" />
	<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
		<c:set var="isOCI" value="true" />
	</sec:authorize>

	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template == 'search'}">
					<%@ include file="/WEB-INF/terrific/modules/metahd-item/metahd-item-search.jsp" %>
				</c:when>
				<c:when test="${template == 'search-404'}">
					<%@ include file="/WEB-INF/terrific/modules/metahd-item/metahd-item-search-404.jsp" %>
				</c:when>
				<c:when test="${template == 'searchemptypage'}">
					<%@ include file="/WEB-INF/terrific/modules/metahd-item/metahd-item-searchemptypage.jsp" %>
				</c:when>
				<c:when test="${template == 'account'}">
					<%@ include file="/WEB-INF/terrific/modules/metahd-item/metahd-item-account.jsp" %>
				</c:when>
				<c:when test="${template == 'lists'}">
					<%@ include file="/WEB-INF/terrific/modules/metahd-item/metahd-item-lists.jsp" %>
				</c:when>
				<c:when test="${template == 'categories'}">
					<%@ include file="/WEB-INF/terrific/modules/metahd-item/metahd-item-categories.jsp" %>
				</c:when>
				<c:when test="${template == 'compare'}">
					<%@ include file="/WEB-INF/terrific/modules/metahd-item/metahd-item-compare.jsp" %>
				</c:when>
				<c:when test="${template == 'cart'}">
					<%@ include file="/WEB-INF/terrific/modules/metahd-item/metahd-item-cart.jsp" %>
				</c:when>
				<c:when test="${template == 'cart-checkout'}">
					<%@ include file="/WEB-INF/terrific/modules/metahd-item/metahd-item-cart-checkout.jsp" %>
				</c:when>
				<c:when test="${template == 'totop'}">
					<%@ include file="/WEB-INF/terrific/modules/metahd-item/metahd-item-totop.jsp" %>
				</c:when>
				<c:when test="${template == 'nav-button'}">
					<%@ include file="/WEB-INF/terrific/modules/metahd-item/metahd-item-nav-button.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/metahd-item/metahd-item.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${template == 'search'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/metahd-item/metahd-item-search.jsp" %>
				</c:when>
				<c:when test="${template == 'search-404'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/metahd-item/metahd-item-search-404.jsp" %>
				</c:when>
				<c:when test="${template == 'searchemptypage'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/metahd-item/metahd-item-searchemptypage.jsp" %>
				</c:when>
				<c:when test="${template == 'account'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/metahd-item/metahd-item-account.jsp" %>
				</c:when>
				<c:when test="${template == 'lists'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/metahd-item/metahd-item-lists.jsp" %>
				</c:when>
				<c:when test="${template == 'categories'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/metahd-item/metahd-item-categories.jsp" %>
				</c:when>
				<c:when test="${template == 'compare'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/metahd-item/metahd-item-compare.jsp" %>
				</c:when>
				<c:when test="${template == 'cart'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/metahd-item/metahd-item-cart.jsp" %>
				</c:when>
				<c:when test="${template == 'cart-checkout'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/metahd-item/metahd-item-cart-checkout.jsp" %>
				</c:when>
				<c:when test="${template == 'totop'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/metahd-item/metahd-item-totop.jsp" %>
				</c:when>
				<c:when test="${template == 'nav-button'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/metahd-item/metahd-item-nav-button.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/OCI/metahd-item/metahd-item.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</terrific:mod>
