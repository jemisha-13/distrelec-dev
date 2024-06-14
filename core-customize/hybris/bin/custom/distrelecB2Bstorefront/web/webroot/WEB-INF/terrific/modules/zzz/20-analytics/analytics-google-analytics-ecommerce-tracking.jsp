<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<%-- Format Total Order Price Value --%>
<c:choose>
	<c:when test="${empty order.subTotal.value}">
		<c:set var="totalCartValue" value="" />
	</c:when>
	<c:otherwise>
		<c:set var="totalCartValue" value="${namicscommerce:formatAnalyticsPrice(order.subTotal)}" />
	</c:otherwise>
</c:choose>

<%-- Format Shipping Costs Value --%>
<c:choose>
	<c:when test="${empty order.deliveryCost.value}">
		<c:set var="shippingCosts" value="" />
	</c:when>
	<c:otherwise>
		<c:set var="shippingCosts" value="${namicscommerce:formatAnalyticsPrice(order.deliveryCost)}" />
	</c:otherwise>
</c:choose>

<%-- Format Tax Costs Value --%>
<c:choose>
	<c:when test="${empty order.totalTax.value}">
		<c:set var="taxCosts" value="" />
	</c:when>
	<c:otherwise>
		<c:set var="taxCosts" value="${namicscommerce:formatAnalyticsPrice(order.totalTax)}" />
	</c:otherwise>
</c:choose>


<!-- Google Analytics -->
<script>

	(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	})(window,document,'script','//www.google-analytics.com/analytics.js','ga');

	ga('create', '${googleAnalyticsTrackingId}');
	ga('send', 'pageview');
	ga('require', 'ecommerce', 'ecommerce.js'); // Load the ecommerce plug-in.
	ga('ecommerce:addTransaction', {
		'id' : '${order.code}', 			// Transaction ID. Required
		'affiliation' : '', 	// Affiliation or store name
		'revenue' : '${totalCartValue}', 	// Grand Total
		'shipping' : '${shippingCosts}', 		// Shipping
		'tax' : '${taxCosts}' 			// Tax
	});
	
	<c:forEach var="entry" items="${order.entries}">
		<%-- get product name --%>
		<c:set var="productName">
		${fn:replace(entry.product.name, "'", "\\'")}
		</c:set>
		
		<%-- Format Product Price Value --%>
		<c:choose>
			<c:when test="${empty entry.basePrice.value}">
				<c:set var="productCost" value="" />
			</c:when>
			<c:otherwise>
				<c:set var="productCost" value="${namicscommerce:formatAnalyticsPrice(entry.basePrice)}" />
			</c:otherwise>
		</c:choose>

		<%-- Get Product Category --%>
		<c:set var="categoriesCount">
			${fn:length(entry.product.categories)}
		</c:set>
		<c:set var="productCategory" value="" />
		<c:if test="${categoriesCount > 0}">
			<c:set var="productCategory">
				${entry.product.categories[categoriesCount-1].name}
			</c:set>
			<c:set var ="productCategory">
				${fn:replace(productCategory, "'", "\\'")}
			</c:set>
		</c:if>
	
		// addItem should be called for every item in the shopping cart.
		ga('ecommerce:addItem', {
			'id' : '${order.code}', 			// Transaction ID. Required
			'name' : '${productName}', 	// Product name. Required
			'sku' : '${entry.product.codeErpRelevant}', 		// SKU/code
			'category' : '${productCategory}', // Category or variation
			'price' : '${productCost}', 		// Unit price
			'quantity' : '${entry.quantity}' 		// Quantity
		});
	</c:forEach>
	
	ga('ecommerce:send'); // Send transaction and item data to Google Analytics.
</script>
<!-- End Google Analytics -->
