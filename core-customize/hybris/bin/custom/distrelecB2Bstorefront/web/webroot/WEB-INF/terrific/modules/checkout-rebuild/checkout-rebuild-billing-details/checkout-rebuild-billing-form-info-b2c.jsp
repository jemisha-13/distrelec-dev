<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ul class="o-cr-editable-form__info-list js-cr-editableform-info-list">
    <c:if test="${showBillingInfoMode}">
        <li id="b2cBillingInfo_title.firstName.lastName"><c:out value="${billingAddress.title}"/> <c:out value="${billingAddress.firstName}"/> <c:out value="${billingAddress.lastName}"/></li>

        <c:if test="${not empty billingAddress.line1}">
            <li id="b2cBillingInfo_line1.line2"><c:out value="${billingAddress.line1}"/><c:if
                    test="${not empty billingAddress.line2}"> <c:out value="${billingAddress.line2}"/></c:if></li>
        </c:if>

        <c:if test="${not empty billingAddress.postalCode}">
            <li id="b2cBillingInfo_postalCode.town"><c:out value="${billingAddress.postalCode}"/> <c:out value="${billingAddress.town}"/>,</li>
        </c:if>

        <c:if test="${not empty billingAddress.country.name}">
            <li id="b2cBillingInfo_countryName"><c:if test="${not empty billingAddress.region.name}"><c:out value="${billingAddress.region.name}"/>, </c:if><c:out value="${billingAddress.country.name}"/></li>
        </c:if>

        <c:choose>
            <c:when test="${not empty billingAddress.cellphone}">
                <li id="b2cBillingInfo_phone"><c:out value="${billingAddress.cellphone}"/></li>
            </c:when>
            <c:otherwise>
                <li id="b2cBillingInfo_phone"><c:out value="${billingAddress.phone1}"/></li>
            </c:otherwise>
        </c:choose>
    </c:if>
</ul>

<script id="tmpl-editable-form" type="text/x-template-dotjs">
    <li id="b2cBillingInfo_title.firstName.lastName">{{= it.title}} {{= it.firstName}} {{= it.lastName}}</li>
    <li id="b2cBillingInfo_line1.line2">{{= it.line1}}{{? it.line2}} {{= it.line2}}{{?}}</li>
    <li id="b2cBillingInfo_postalCode.town">{{= it.postalCode}} {{= it.town}},</li>
    <li id="b2cBillingInfo_countryName">{{? it.region}}{{= it.region.name}}, {{?}}{{= it.country.name}}</li>
    <li id="b2cBillingInfo_phone">{{? it.cellphone}}{{= it.cellphone}}{{??}}{{= it.phone1}}{{?}}</li>
</script>
