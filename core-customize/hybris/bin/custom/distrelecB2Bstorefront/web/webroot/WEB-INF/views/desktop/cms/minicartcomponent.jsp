<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<c:url value="/cart/miniCart/${totalDisplay}" var="refreshMiniCartUrl"/>
<c:url value="/cart/rollover/${component.uid}" var="rolloverPopupUrl"/>

<script id="miniCartTemplate" type="text/x-jquery-tmpl">
/*<![CDATA[*/
	<dt>
		<ycommerce:testId code="miniCart_items_label">
			<spring:theme text="items" code="cart.items" arguments="{{= totalItems}}"/>
		</ycommerce:testId>
		 -
	</dt>
	<dd>
		<ycommerce:testId code="miniCart_total_label">
			<c:if test="${totalDisplay == 'TOTAL'}">
				{{= totalPrice.formattedValue}}
			</c:if>
			<c:if test="${totalDisplay == 'SUBTOTAL'}">
				{{= subTotal.formattedValue}}
			</c:if>
			<c:if test="${totalDisplay == 'TOTAL_WITHOUT_DELIVERY'}">
				{{= totalNoDelivery.formattedValue}}
			</c:if>
		</ycommerce:testId>
	</dd>
/*]]>*/
</script>

<script type="text/javascript">
/*<![CDATA[*/
function refreshMiniCart() {
	$.get('${refreshMiniCartUrl}', function(result) {
		$('#minicart_data').html(result)
	});
}

$(document).ready(function() {

	$('#rollover_cart_popup').hide();

	$('#cart_content').hover(
			function() { $.data(this, 'hover', true); },
			function() { $.data(this, 'hover', false); }
	).data('hover', false);

	$('#rollover_cart_popup').hover(
			function() { $.data(this, 'hover', true); },
			function() { $.data(this, 'hover', false); }
	).data('hover', false);

	$('#cart_content').mouseenter(function() {
		$('#cart_popup').hide();
		$.get('${rolloverPopupUrl}', function(result){
			$('#rollover_cart_popup').html(result);
			$('#ajax_cart_close').click(function(e) {
				e.preventDefault();
				$('#rollover_cart_popup').hide();
			});
			$('#rollover_cart_popup').fadeIn();
		});
	});
	$('#cart_content').mouseleave(function() {
		setTimeout(function() {
			if (!$('#cart_content').data('hover') && !$('#rollover_cart_popup').data('hover')) {
				$('#rollover_cart_popup').fadeOut();
			}
		}, 100);
	});

	$('#rollover_cart_popup').mouseenter(function() {
		$('#rollover_cart_popup').show();
	});
	$('#rollover_cart_popup').mouseleave(function() {
		setTimeout(function() {
			if(!$('#cart_content').data('hover') && !$('#rollover_cart_popup').data('hover')) {
				$('#rollover_cart_popup').fadeOut();
			}
		}, 100);
	});
});
/*]]>*/
</script>


<div id="cart_header">
	<div id="cart_content">
		<h2>${component.title}</h2>
		<spring:theme code="minicart.cart.alt" text="" var="minicartCartAlt"/>
		<theme:image code="img.iconCart" alt="${minicartCartAlt}" title="${minicartCartAlt}" />
		<dl id="minicart_data">
			<dt>
				<ycommerce:testId code="miniCart_items_label">
					<spring:theme text="items" code="cart.items" arguments="${totalItems}"/>
				</ycommerce:testId>
				-
			</dt>
			<dd>
				<ycommerce:testId code="miniCart_total_label">
					<c:if test="${totalDisplay == 'TOTAL'}">
						<format:price priceData="${totalPrice}"/>
					</c:if>
					<c:if test="${totalDisplay == 'SUBTOTAL'}">
						<format:price priceData="${subTotal}"/>
					</c:if>
					<c:if test="${totalDisplay == 'TOTAL_WITHOUT_DELIVERY'}">
						<format:price priceData="${totalNoDelivery}"/>
					</c:if>
				</ycommerce:testId>
			</dd>
		</dl>
	</div>
	<ul>
		<li>
			<p>
				<ycommerce:testId code="miniCart_viewCart_link">
					<c:url value="/cart" var="cartUrl"/>
					<a href="${cartUrl}"><spring:theme text="VIEW CART" code="cart.view"/></a>
				</ycommerce:testId>
			</p>
		</li>
		<li class="active">
			<p>
				<ycommerce:testId code="miniCart_checkout_link">
					<c:url value="/cart/checkout" var="checkoutUrl"/>
					<a href="${checkoutUrl}"><spring:theme text="CHECKOUT" code="cart.checkout"/></a>
				</ycommerce:testId>
			</p>
		</li>
	</ul>
</div>
<cart:rolloverCartPopup/>