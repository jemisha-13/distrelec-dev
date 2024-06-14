<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="customerType"
       value="${cartData.b2bCustomerData.customerType eq 'GUEST' or cartData.b2bCustomerData.customerType eq 'B2C' ? 'b2c' : 'b2b'}"/>

<h2 class="head">
    <c:choose>
        <c:when test="${isEShopGroup}">
            <spring:message code="checkout.address.b2b.shipping.address.b2e" text="Billing and Shipping Address"/>
        </c:when>
        <c:otherwise>
            <spring:message code="checkout.address.${customerType}.billing.address" text="Billing Address"/>
        </c:otherwise>
    </c:choose>
</h2>

<c:choose>
    <c:when test="${isEShopGroup eq false}">
        <mod:address template="billing-${customerType}"
                     skin="billing" address="${billingAddresses[0]}"
                     addressEditMode="${isExportShop eq true ? 'false' : 'true'}"
                     customerType="${cartData.b2bCustomerData.customerType}"
        />
    </c:when>
    <c:otherwise>
        <mod:address
                template="billing-eshop-b2b"
                skin="billing" address="${billingAddresses[0]}"
                addressEditMode="${keyAccountUser or isExportShop ? 'false' : 'true'}"
                customerType="${cartData.b2bCustomerData.customerType}"/>
    </c:otherwise>
</c:choose>
	

