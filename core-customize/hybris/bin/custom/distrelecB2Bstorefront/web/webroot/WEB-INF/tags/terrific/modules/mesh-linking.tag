<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="sourceCategoryName" description="The source category name" type="java.lang.String" required="false" rtexprvalue="true" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Specific module settings --%>
<%@ tag description="Module: mesh-linking - Templates: default, product, category, manufacturer" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<%-- Module template selection --%>
<terrific:mod name="related-pages" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template eq 'product'}">
					<%@ include file="/WEB-INF/terrific/modules/mesh-linking/mesh-linking-product.jsp" %>
				</c:when>
				<c:when test="${template eq 'category'}">
					<%@ include file="/WEB-INF/terrific/modules/mesh-linking/mesh-linking-category.jsp" %>
				</c:when>
				<c:when test="${template eq 'manufacturer'}">
					<%@ include file="/WEB-INF/terrific/modules/mesh-linking/mesh-linking-manufacturer.jsp" %>
				</c:when>
				<c:when test="${template eq 'productlistlinks'}">
					<%@ include file="/WEB-INF/terrific/modules/mesh-linking/mesh-linking-productlistlinks.jsp" %>
				</c:when>
				<c:when test="${template eq 'categorylinks'}">
					<%@ include file="/WEB-INF/terrific/modules/mesh-linking/mesh-linking-categorylinks.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/mesh-linking/mesh-linking.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${template eq 'product'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/mesh-linking/mesh-linking-product.jsp" %>
				</c:when>
				<c:when test="${template eq 'category'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/mesh-linking/mesh-linking-category.jsp" %>
				</c:when>
				<c:when test="${template eq 'manufacturer'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/mesh-linking/mesh-linking-manufacturer.jsp" %>
				</c:when>
				<c:when test="${template eq 'productlistlinks'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/mesh-linking/mesh-linking-productlistlinks.jsp" %>
				</c:when>
				<c:when test="${template eq 'categorylinks'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/mesh-linking/mesh-linking-categorylinks.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/OCI/mesh-linking/mesh-linking.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</terrific:mod>
