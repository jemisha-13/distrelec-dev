<%-- Common module settings --%>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="cartData" required="false" type="de.hybris.platform.commercefacades.order.data.CartData" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="href" type="java.lang.String" rtexprvalue="true" %>

<%-- Specific module settings --%>
<%@ tag description="Module: checkout-rebuild - Templates: default" %>

<%-- Module template selection --%>
<terrific:mod name="checkout-rebuild" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<div class="mod-checkout-rebuild__content">
		<div class="container">
			<div class="row">
				<jsp:doBody />
			</div>
		</div>
	</div>

	<div class="js-sticky-sidebar-stop"></div>
</terrific:mod>
