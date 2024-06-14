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

<%-- Module Specific Attributes --%>
<%@ attribute name="downloadUrl" required="false" type="java.lang.String" %>
<%@ attribute name="exportId" required="false" type="java.lang.String" %>
<%@ attribute name="productsInListCount" required="false" type="java.lang.Integer" %>

<%-- Module template selection --%>
<terrific:mod name="productlist-tools" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
        <c:when test="${template == 'favorite'}">
            <%@ include file="/WEB-INF/terrific/modules/productlist-tools/productlist-tools-favorite.jsp" %>
        </c:when>
		<c:when test="${template == 'shopping'}">
			<%@ include file="/WEB-INF/terrific/modules/productlist-tools/productlist-tools-shopping.jsp" %>
		</c:when>
		<c:when test="${template == 'compare'}">
			<%@ include file="/WEB-INF/terrific/modules/productlist-tools/productlist-tools-compare.jsp" %>
		</c:when>
		<c:when test="${template == 'order-history'}">
			<%@ include file="/WEB-INF/terrific/modules/productlist-tools/productlist-tools-order-history.jsp" %>
		</c:when>		
		<c:when test="${template == 'invoice-history'}">
			<%@ include file="/WEB-INF/terrific/modules/productlist-tools/productlist-tools-invoice-history.jsp" %>
		</c:when>
		<c:when test="${template == 'quotation-history'}">
			<%@ include file="/WEB-INF/terrific/modules/productlist-tools/productlist-tools-quotation-history.jsp" %>
		</c:when>
		<c:when test="${template == 'approval-request'}">
			<%@ include file="/WEB-INF/terrific/modules/productlist-tools/productlist-tools-approval-request.jsp" %>
		</c:when>
        <c:otherwise>
           	<%@ include file="/WEB-INF/terrific/modules/productlist-tools/productlist-tools.jsp" %>
        </c:otherwise>
    </c:choose>	
</terrific:mod>
