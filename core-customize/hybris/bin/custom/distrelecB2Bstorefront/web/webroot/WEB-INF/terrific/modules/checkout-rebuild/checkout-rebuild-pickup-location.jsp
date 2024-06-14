<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>

<div class="o-cr-editable-form is-info-mode js-cr-editableform">
    <div class="o-cr-editable-form__info js-cr-editableform-info">
        <ul class="o-cr-editable-form__info-list js-cr-editableform-info-list">
            <li id="checkoutPickupLocationName"><c:out value="${pickupWarehouses[0].name}"/></li>

            <c:if test="${not empty pickupWarehouses[0].streetName}">
                <li id="checkoutPickupLocationStreet"><c:out value="${pickupWarehouses[0].streetName}"/>&nbsp;<c:out value="${pickupWarehouses[0].streetNumber}"/></li>
            </c:if>

            <c:if test="${not empty pickupWarehouses[0].postalCode}">
                <li id="checkoutPickupLocationPostalCode"><c:out value="${pickupWarehouses[0].postalCode}"/>&nbsp;<c:out value="${pickupWarehouses[0].town}"/>,</li>
            </c:if>

            <c:if test="${not empty pickupWarehouses[0].phone}">
                <li id="checkoutPickupLocationPhone"><c:out value="${pickupWarehouses[0].phone}"/></li>
            </c:if>

            <c:if test="${not empty pickupWarehouses[0].openingsHourMoFr}">
                <li id="checkoutPickupLocationOpenings"><spring:message code="checkout.rebuild.pickup.opening.hours"/>&nbsp;<spring:message
                        code="checkout.rebuild.pickup.opening.hours.mon.fri"/>&nbsp;<c:out
                        value="${pickupWarehouses[0].openingsHourMoFr}"/><c:if
                        test="${not empty pickupWarehouses[0].openingsHourSa}">&nbsp;/&nbsp;<c:out
                        value="${pickupWarehouses[0].openingsHourSa}"/></c:if></li>
            </c:if>
        </ul>
    </div>
</div>
