<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>

<script type="text/javascript">
/*<![CDATA[*/
function submitRemove(id){
	var productCode = $('#updateCartForm'+id).get(0).productCode.value;
	var initialCartQuantity = $('#updateCartForm'+id).get(0).initialQuantity.value;
	
	if(window.trackRemoveFromCart) {
		trackRemoveFromCart(productCode, initialCartQuantity);
	}
	
	$('#updateCartForm'+id).get(0).quantity.value = 0;
	$('#updateCartForm'+id).get(0).initialQuantity.value = 0;
	$('#updateCartForm'+id).get(0).submit();
}
function submitUpdate(id){
	var productCode = $('#updateCartForm'+id).get(0).productCode.value;
	var initialCartQuantity = $('#updateCartForm'+id).get(0).initialQuantity.value;
	var newCartQuantity = $('#updateCartForm'+id).get(0).quantity.value;
	
	if(window.trackUpdateCart) {
		trackUpdateCart(productCode, initialCartQuantity, newCartQuantity);
	}
	
	$('#updateCartForm'+id).get(0).submit();
}
/*]]>*/
</script>

<div class="item_container_holder">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h2><spring:theme code="basket.page.title.yourItems"/></h2>
	</div>
	<div class="item_container">
		<span class="cart_id"><spring:theme code="basket.page.cartId"/> <span class="cart-id-nr">${cartData.code}</span></span>
		<table id="your_cart">
			<thead>
				<tr>
					<th id="header2"><span class="hidden"><spring:theme code="basket.page.title"/></span></th>
					<th id="header3"><spring:theme code="basket.page.quantity"/></th>
					<th id="header4"><spring:theme code="basket.page.itemPrice"/></th>
					<th id="header5"><spring:theme code="basket.page.total"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${cartData.entries}" var="entry">
					<c:url value="${entry.product.url}" var="productUrl"/>
					<tr>
						<td headers="header2" class="product_details">
							<span class="product_image">
								<a href="${productUrl}">
									<product:productPrimaryImage product="${entry.product}" format="thumbnail"/>
								</a>
							</span>
							<ycommerce:testId code="cart_product_name">
								<h2><a href="${productUrl}">${entry.product.name}</a></h2>
							</ycommerce:testId>
							<c:forEach items="${entry.product.baseOptions}" var="option">
								<c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
									<c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
										<dl>
											<dt>${selectedOption.name}:</dt>
											<dd>${selectedOption.value}</dd>
										</dl>
									</c:forEach>
								</c:if>
							</c:forEach>							
							<c:if test="${not empty cartData.potentialProductPromotions}">
								<ul class="cart-promotions">
									<c:forEach items="${cartData.potentialProductPromotions}" var="promotion">
										<c:set var="displayed" value="false"/>
										<c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
											<c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber
											&& not empty promotion.description}">
												<c:set var="displayed" value="true"/>
												<li class="cart-promotions-potential">
													<ycommerce:testId code="cart_promotion_label">
														<span>${promotion.description}</span>
													</ycommerce:testId>
												</li>
											</c:if>
										</c:forEach>
									</c:forEach>
								</ul>
							</c:if>
							<c:if test="${not empty cartData.appliedProductPromotions}">
								<ul class="cart-promotions">
									<c:forEach items="${cartData.appliedProductPromotions}" var="promotion">
										<c:set var="displayed" value="false"/>
										<c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
											<c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
												<c:set var="displayed" value="true"/>
												<li class="cart-promotions-applied">
													<ycommerce:testId code="cart_appliedPromotion_label">
														<span>${promotion.description}</span>
													</ycommerce:testId>
												</li>
											</c:if>
										</c:forEach>
									</c:forEach>
								</ul>
							</c:if>
						</td>
						<td headers="header3" class="quantity">
							<c:url value="/cart/update" var="cartUpdateFormAction" />
							<form:form id="updateCartForm${entry.entryNumber}" action="${cartUpdateFormAction}" method="post" modelAttribute="updateQuantityForm${entry.product.code}">
								<input type="hidden" name="entryNumber" value="${entry.entryNumber}"/>
								<input type="hidden" name="productCode" value="${entry.product.code}"/>
								<input type="hidden" name="initialQuantity" value="${entry.quantity}"/>
								
								<ycommerce:testId code="cart_product_quantity">
									<form:label cssClass="skip" path="quantity"><spring:theme code="basket.page.quantity"/></form:label>
									<form:input disabled="${not entry.updateable}"  type="text" size="1" name="quantity" class="qty" value="${entry.quantity}" path="quantity"/>
								</ycommerce:testId>
								<c:if test="${entry.updateable}" >	
									<ycommerce:testId code="cart_product_updateQuantity">
										<a href="javascript:submitUpdate(${entry.entryNumber});"><spring:theme code="basket.page.update"/></a>
									</ycommerce:testId>
								</c:if>
							</form:form>
							<c:if test="${entry.updateable}" >	
								<ycommerce:testId code="cart_product_removeProduct">
									<a href="javascript:submitRemove(${entry.entryNumber});">
										<spring:theme code="text.iconCartRemove" var="iconCartRemove"/>
										<theme:image code="img.iconCartRemove" alt="${iconCartRemove}" title="${iconCartRemove}"/>
									</a>
								</ycommerce:testId>
							</c:if>
						</td>
						<td headers="header4">
							<format:price priceData="${entry.basePrice}" displayFreeForZero="true"/>
						</td>
						<td headers="header5" class="total">
							<ycommerce:testId code="cart_totalProductPrice_label">
								<format:price priceData="${entry.totalPrice}" displayFreeForZero="true"/>
							</ycommerce:testId>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>
