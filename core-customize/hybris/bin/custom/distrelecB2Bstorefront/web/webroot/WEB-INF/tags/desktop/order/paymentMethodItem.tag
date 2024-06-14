<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="order" required="true"
	type="de.hybris.platform.commercefacades.order.data.AbstractOrderData"%>
<%@ taglib prefix="ordertl" tagdir="/WEB-INF/tags/desktop/order"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<div class="item_container_holder positive">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h2>
			<spring:theme code="text.paymentMethod" text="Payment Method" />
		</h2>
	</div>

	<div class="item_container">
		<c:if test="${order.paymentType.code eq 'CARD'}">
			<ordertl:paymentMethodItemOnCreditCard order="${order}"/>
		</c:if>
		<c:if test="${order.paymentType.code eq 'ACCOUNT'}">
			<ordertl:paymentMethodItemOnAccount order="${order}"/>
		</c:if>
	</div>
</div>
