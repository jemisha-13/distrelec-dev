<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<input type="hidden" class="hidden-product-code" value="${product.code}" />
<input type="hidden" class="hidden-entry-number" value="${orderEntry.entryNumber}" />
<spring:message code="text.store.dateformat.datepicker.selection" var="sDateFormat" />


<div id="error-field" class="error-message js-error-message hidden">
	<spring:message code="cart.return.items.form.error" />
</div>

<div class="error-qty hidden">
	<spring:message code="cart.return.items.form.error" />
</div>

<form:form modelAttribute="createRMARequestForm"  method="post" action="${returnOnlineAction}" cssClass="form-rma js-rma-form">
	<input type="hidden" class="hidden" name="orderId" value="${orderData.code}"/>
	<input type="hidden" class="hidden" name="returnID" value="${orderData.code}"/>

	<div class="row return-items-buttons return-items-buttons--second">
		<div class="gu-3 ">
			<a href="/my-account/order-history/order-details/${orderData.code}/" class="btn btn-secondary btn-change buttonCancel"><i class="fa fa-angle-left" aria-hidden="true"></i><span><spring:message code="text.account.orderHistory.orderDetails.returnItems.cancel" /></span></a>
		</div>
	</div>

	<ul class="cart-list loading border-bottom-grey">
		<c:forEach var="orderEntry" items="${orderData.entries}" varStatus="status">
			<c:set var="isDummyItem" value="${orderEntry.dummyItem}" />
			<mod:cart-list-item orderData="${orderData}" template="return-items" skin="return-items" tag="li" htmlClasses="row cart-list-item${isDummyItem ? ' dummy-item' : '' } js-rma-item" orderEntry="${orderEntry}" loopNumber="${status.index}" isDummyItem="${isDummyItem}" />
		</c:forEach>
	</ul>

	<div class="row return-items-buttons">
		<div class="gu-3 ">
			<a href="/my-account/order-history/order-details/${orderData.code}/" class="btn btn-secondary btn-change buttonCancel"><i class="fa fa-angle-left" aria-hidden="true"></i><span><spring:message code="text.account.orderHistory.orderDetails.returnItems.cancel" /></span></a>
		</div>

		<c:if test="${canSubmitRmaRequest}">
            <div class="gu-9">
                <button class="btn btn-primary btn-return-items js-rma-form-submit"
						type="button"
						disabled="true"
                        data-aainteraction="submit return order"
                        data-aaorder-id="${orderData.code}"
                        data-aalocation="return items">
                    <span><spring:message code="text.account.orderHistory.orderDetails.returnItems.continue" /></span>
                    <i class="fa fa-angle-right" aria-hidden="true"></i>
                </button>
            </div>
        </c:if>
	</div>
</form:form>

<!-- tmpl -->
<div class="hidden">
	<mod:cart-list-item template="dot-tmpl" />
</div>

<div class="hidden" id="tmpl-stock_level_pickup_header">
	<div class="row line"></div>
	<div class="row">
		<h2><spring:message code="product.shipping.in.store.pickup" /></h2>
	</div>
</div>

<script id="tmpl-stock_level_pickup_row" type="text/x-dot-template">
	{{~it :item:id }}
	<div class="row">
		<div class="left">{{= item.warehouseName}}</div>
		<div class="right">{{= item.stockLevel}}</div>
	</div>
	{{~}}
</script>

<script id="tmpl-stock_level" type="text/x-dot-template">
	<div class="row">
		<h2><spring:message code="product.shipping" /></h2>
	</div>
	<div class="row">
		<div class="left">24h</div>
		<div class="right">{{= it.stockLevel24h}}</div>
	</div>
	<div class="row">
		<div class="left">< 10 d</div>
		<div class="right">{{= it.stockLevel48h}}</div>
	</div>
	<div class="row">
		<div class="left">> 10 d{{= it.backorderQuantityFormated}}</div>
		<div class="right">{{= it.backorderQuantity}}</div>
	</div>
</script>
<!-- end tmpl -->