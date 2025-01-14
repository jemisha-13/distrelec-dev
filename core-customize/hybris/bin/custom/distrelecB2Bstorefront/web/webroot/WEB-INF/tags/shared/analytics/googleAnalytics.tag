<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${not empty googleAnalyticsTrackingId}">
<script type="text/javascript">
/* Google Analytics */

var googleAnalyticsTrackingId = '${googleAnalyticsTrackingId}';
var _gaq = _gaq || [];
_gaq.push(['_setAccount', googleAnalyticsTrackingId]);

<c:choose>
	<c:when test="${pageType.value == 'ProductSearch' || pageType.value == 'Category'}">
		<c:choose>
			<c:when test="${searchPageData.pagination.totalNumberOfResults > 0}">
				<c:if test="${not empty searchPageData.filters}">
					<c:forEach items="${searchPageData.filters}" var="breadcrumb">
						_gaq.push(['_trackEvent', 'facet', '${breadcrumb.facetName}', '${breadcrumb.facetValueName}']);
					</c:forEach>
				</c:if>
			</c:when>
			
			<c:otherwise>
				_gaq.push(['_setCustomVar', 1, 'ZeroResults', '${searchPageData.freeTextSearch}', 3]);
			</c:otherwise>
		</c:choose>
		
		_gaq.push(['_trackPageview']);
	</c:when>
	<c:when test="${pageType.value == 'OrderConfirmation'}">
		_gaq.push([
	 		 '_addTrans',
	 		 '${orderData.code}',
	 		 '${siteName}',
	 		 '${orderData.totalPrice.value}',
	 		 '${orderData.totalTax.value}',
	 		 '${orderData.deliveryCost.value}',
	 		 '${orderData.deliveryAddress.town}',
	 		 '${orderData.deliveryAddress.postalCode}',
	 		 '${orderData.deliveryAddress.country.name}'
	 	]);
	 	<c:forEach items="${orderData.entries}" var="entry">
	 		_gaq.push([
	 		    '_addItem',
	 			'${orderData.code}',
	 			'${entry.product.code}',
	 			'${entry.product.name}',
	 			<c:choose>
		 			<c:when test="${not empty entry.product.categories}">
		 				'${entry.product.categories[fn:length(entry.product.categories) - 1].name}',
		 			</c:when>
		 			<c:otherwise>
		 				'',
		 			</c:otherwise>
	 			</c:choose>
	 			'${entry.product.price.value}',
	 			'${entry.quantity}'
	 		]);
	 	</c:forEach>
	 	_gaq.push(['_trackTrans']);
	</c:when>
	<c:otherwise>
		//
	</c:otherwise>
</c:choose>

(function() {
	var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();


function trackAddToCart_google(productCode, quantityAdded) {
	_gaq.push(['_trackEvent', 'Cart', 'AddToCart', productCode, quantityAdded]);
}

function trackUpdateCart(productCode, initialQuantity, newQuantity) {
	if (initialQuantity != newQuantity) {
		if (initialQuantity > newQuantity) {
			_gaq.push(['_trackEvent', 'Cart', 'RemoveFromCart', productCode, initialQuantity - newQuantity]);
		} else {
			_gaq.push(['_trackEvent', 'Cart', 'AddToCart', productCode, newQuantity - initialQuantity]);
		}
	}
}

function trackRemoveFromCart(productCode, initialQuantity) {
	_gaq.push(['_trackEvent', 'Cart', 'RemoveFromCart', productCode, initialQuantity]);
}
</script>
</c:if>