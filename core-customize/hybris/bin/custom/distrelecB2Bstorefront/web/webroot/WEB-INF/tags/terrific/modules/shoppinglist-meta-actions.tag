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

<%@ attribute name="shoppingListsList" type="java.util.List" required="true" %>
<%@ attribute name="favoriteListItemCount" type="java.lang.Integer" required="true" %>
<%@ attribute name="favoriteListId" type="java.lang.String" required="true" %>
<%@ attribute name="currentList" type="com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData" required="true" %>

<%-- Module template selection --%>
<terrific:mod name="shoppinglist-meta-actions" tag="nav" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<%@ include file="/WEB-INF/terrific/modules/shoppinglist-meta-actions/shoppinglist-meta-actions.jsp" %>
</terrific:mod>
