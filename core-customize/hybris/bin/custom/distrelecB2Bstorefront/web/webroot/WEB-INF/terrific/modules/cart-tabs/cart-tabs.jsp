<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>

<c:set var="loginUrl" value="/login" />
<c:set var="ShoppingListUrl" value="/shopping" />
<c:set var="importToolUrl" value="/bom-tool" />

<c:set var="isEShopGroup" value="false"/>
<sec:authorize access="hasRole('ROLE_B2BEESHOPGROUP')">
    <c:set var="isEShopGroup" value="true"/>
</sec:authorize>

<spring:message code="cart.directorder.direct.order" var="sQuickOrderTitle" text="Quick order" />
<spring:message code="cartTabs.btn.selectShoppingList" var="sSelectShoppingList" text="Select shopping list" />
<spring:message code="cartTabs.btn.importShoppingImport" var="sImportShoppingList" text="Import shopping list" />
<spring:message code="cartTabs.title.shoppingList" var="sShoppingList" text="Shopping list" />
<spring:message code="cartTabs.title.import" var="sImportTitle" text="Import" />
<spring:message code="cartTabs.loggedIn.message" arguments="${loginUrl}" var="sLoggedInMessage" text="You must be logged in to use this feature" />
<spring:message code="cartTabs.signIn.text" var="sSignIn" text="Sign in here" />


<div class="tabs-holder">
    <div class="tabs-holder__header">
        <div class="tabs-holder__header__item tabs-holder__header__item--active" data-tab-id="1">
            ${sQuickOrderTitle}
        </div>
            <div class="tabs-holder__header__item ${isEShopGroup ? ' tabs-holder__content__item--disabled' : ''}" data-tab-id="2">
                ${sShoppingList}
            </div>
            <div class="tabs-holder__header__item" data-tab-id="3">
                ${sImportTitle}
            </div>
    </div>
    <div class="tabs-holder__content">
        <div class="tabs-holder__content__item tabs-holder__content__item--active" data-tab-id="1">
            <c:if test="${empty EditCart or EditCart}">
                <mod:cart-directorder/>
            </c:if>
        </div>
            <div class="tabs-holder__content__item tabs-holder__content__item--select" data-tab-id="2">
            <sec:authorize access="!hasRole('ROLE_CUSTOMERGROUP') and !hasRole('ROLE_B2BADMINGROUP')">
                <a href="${ShoppingListUrl}" class="mat-button mat-button--action-red disabled">
                    ${sSelectShoppingList}
                </a>
                <p>
                    ${sLoggedInMessage}
                </p>
            </sec:authorize>
            <sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
                <a href="${ShoppingListUrl}" class="mat-button mat-button--action-red">
                    ${sSelectShoppingList}
                </a>
            </sec:authorize>
        </div>
            <div class="tabs-holder__content__item tabs-holder__content__item--select" data-tab-id="3">
            <sec:authorize access="!hasRole('ROLE_CUSTOMERGROUP') and !hasRole('ROLE_B2BADMINGROUP')">
                <a href="${importToolUrl}" class="mat-button mat-button--action-red disabled">
                    ${sImportShoppingList}
                </a>
                <p>
                    ${sLoggedInMessage}
                </p>
            </sec:authorize>
            <sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
                <a href="${importToolUrl}" class="mat-button mat-button--action-red">
                    ${sImportShoppingList}
                </a>
            </sec:authorize>
        </div>
    </div>
    <div class="cart-holder__cta">
        <sec:authorize access="!hasRole('ROLE_CUSTOMERGROUP') and !hasRole('ROLE_B2BADMINGROUP')">
            <div class="cta cta--full">
                <cms:slot var="feature" contentSlot="${slots.CartRegistration}">
                    <cms:component component="${feature}" />
                </cms:slot>
            </div>
        </sec:authorize>
        <sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
            <div class="cta cta--half">
                <cms:slot var="feature" contentSlot="${slots.CartShoppingList}">
                    <cms:component component="${feature}" />
                </cms:slot>
            </div>
            <div class="cta cta--half">
                <cms:slot var="feature" contentSlot="${slots.CartImportListContent}">
                    <cms:component component="${feature}" />
                </cms:slot>
            </div>
        </sec:authorize>
    </div>
</div>