<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Specific module settings --%>
<%@ tag description="Module: toolsitem - Templates: default" %>
<%@ attribute name="productId" description="The id of the product" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="productMinQuantity" description="The minimum order of the product" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="listId" description="The id of the shoppinglist which contains the product" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="message" description="Message for popopver" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="title" description="Title for popopver" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="downloadUrl" required="false" type="java.lang.String" %>
<%@ attribute name="exportId" required="false" type="java.lang.String" %>
<%@ attribute name="warehouseCode" required="false" type="java.lang.String" %>
<%@ attribute name="dataLocation" required="false" type="java.lang.String" %>
<%@ attribute name="positionIndex" description="The index of the product lisiting count" type="java.lang.String" required="false" %>
<%@ attribute name="orderIndex" required="false" type="java.lang.String" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<%-- Module template selection --%>
<terrific:mod name="toolsitem" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template == 'toolsitem-favorite'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-favorite.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-favorite-remove'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-favorite-remove.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-compare'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-compare.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-compare-plp'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-compare-plp.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-compare-popup-plp'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-compare-popup-plp.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-compare-remove'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-compare-remove.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-compare-remove-all'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-compare-remove-all.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-compare-remove-all-plp'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-compare-remove-all-plp.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-shopping'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-shopping.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-shopping-plp'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-shopping-plp.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-shopping-bom'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-shopping-bom.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-shopping-remove'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-shopping-remove.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-cart'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-cart.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-bom-remove'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-bom-remove.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-print'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-print.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-print-cart'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-print-cart.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-print-button'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-print-button.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-print-button-rma-create'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-print-button-rma-create.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-gallery-loupe'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-gallery-loupe.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-share'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-share.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-share-email-only'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-share-email-only.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-share-email-only-cart'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-share-email-only-cart.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-download'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-download.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-download-cart'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-download-cart.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-upload'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-upload.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-fb-like'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-fb-like.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-notice'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-notice.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-information'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-information.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-lbl-cart-bulk'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-lbl-cart-bulk.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-lbl-compare'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-lbl-compare.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-lbl-empty-cart'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-lbl-empty-cart.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-lbl-favorite'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-lbl-favorite.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-lbl-shopping'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-lbl-shopping.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-lbl-shopping-bulk'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-lbl-shopping-bulk.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-lbl-shopping-bulk-cart'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-lbl-shopping-bulk-cart.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-lbl-order-detail-shopping-bulk'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-lbl-order-detail-shopping-bulk.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-availability'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-availability.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-lbl-return-items'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-lbl-return-items.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-favorite-homepage'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-favorite-homepage.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-favorite-compare'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-favorite-compare.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-favorite-plp'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-favorite-plp.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-shopping-compare'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-shopping-compare.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-cart-compare'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-cart-compare.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-print-compare'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-print-compare.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-shoppinglist-remove'}">
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem-shoppinglist-remove.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/toolsitem/toolsitem.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${template == 'toolsitem-compare-plp'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/toolsitem/toolsitem-compare-plp.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-compare-popup-plp'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/toolsitem/toolsitem-compare-popup-plp.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-download-cart'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/toolsitem/toolsitem-download-cart.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-lbl-empty-cart'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/toolsitem/toolsitem-lbl-empty-cart.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-lbl-shopping-bulk-cart'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/toolsitem/toolsitem-lbl-shopping-bulk-cart.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-print-cart'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/toolsitem/toolsitem-print-cart.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-share-email-only-cart'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/toolsitem/toolsitem-share-email-only-cart.jsp" %>
				</c:when>
				<c:when test="${template == 'toolsitem-shopping-plp'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/toolsitem/toolsitem-shopping-plp.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/OCI/toolsitem/toolsitem-cart.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</terrific:mod>
