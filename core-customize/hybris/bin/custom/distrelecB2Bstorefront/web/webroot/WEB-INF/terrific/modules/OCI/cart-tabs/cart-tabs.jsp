<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>

<spring:message code="cart.directorder.direct.order" var="sQuickOrderTitle" text="Quick order" />

<div class="tabs-holder">
    <div class="tabs-holder__header">
        <div class="tabs-holder__header__item tabs-holder__header__item--active" data-tab-id="1">
            ${sQuickOrderTitle}
        </div>
    </div>
    <div class="tabs-holder__content">
        <div class="tabs-holder__content__item tabs-holder__content__item--active" data-tab-id="1">
            <c:if test="${empty EditCart or EditCart}">
                <mod:cart-directorder/>
            </c:if>
        </div>
    </div>
</div>