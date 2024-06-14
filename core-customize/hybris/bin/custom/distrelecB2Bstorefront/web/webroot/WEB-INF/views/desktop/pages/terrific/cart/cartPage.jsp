<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="isOCI" value="false" />
<c:set var="isLoggedIn" value="false" />
<c:set var="customerType" value="${cartData.b2bCustomerData.customerType}"/>
<c:set var="isEShopGroup" value="false"/>

<sec:authorize access="hasRole('ROLE_B2BEESHOPGROUP')">
    <c:set var="isEShopGroup" value="true"/>
</sec:authorize>

<sec:authorize access="hasRole('ROLE_OCICUSTOMERGROUP') or hasRole('ROLE_ARIBACUSTOMERGROUP') or hasRole('ROLE_CXMLCUSTOMERGROUP') or hasRole('ROLE_EPROCUREMENTGROUP')">
    <c:set var="isOCI" value="true" />
</sec:authorize>

<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
    <c:set var="isLoggedIn" value="true" />
</sec:authorize>

<spring:theme code="product.tabs.relatedproducts" text="Back" var="sRelatedProducts"/>

<c:set var="showLightBox" value="${eol or punchout}"/>
<spring:message code="cart.product.clearedOut.title" var="lightboxTitle"/>
<c:choose>
    <c:when test="${eol and punchout}">
        <spring:message code="cart.product.clearedOut.message.checkout.punchoutAndEol" arguments="${eolProducts};${punchedOutProducts}" argumentSeparator=";" var="lightboxMessage"/>
    </c:when>
    <c:when test="${eol}">
        <spring:message code="cart.product.clearedOut.message.checkout.eol" arguments="${eolProducts}" var="lightboxMessage"/>
    </c:when>
    <c:when test="${punchout}">
        <spring:message code="cart.product.clearedOut.message.checkout.punchout" arguments="${punchedOutProducts}" var="lightboxMessage"/>
    </c:when>
</c:choose>


<c:set var="totalProducts" value="${fn:length(cartData.entries)}" />
<spring:message code="cart.product.clearedOut.buttonOk" var="lightboxConfirmButtonText"/>
<spring:theme code="product.eol.alternative" text="Alternative" var="sAlternativeProduct"/>
<spring:theme code="cart.empty.continueShopping" text="Continue Shoppping" var="sContinueShopping"/>
<spring:message code="cart.title.empty" text="Your shopping cart is empty" var="sTitleEmpty"/>
<spring:message code="cart.item.total" text="You have x item(s) in your cart" arguments="${totalProducts}" var="sItemTotal"/>
<spring:message code="cart.text.empty" text="You can add products to you cart quickly using the quick order form below" var="sTextEmpty"/>
<c:set var="homeUrl" value="/" />

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-cart skin-layout-wide skin-layout-nonavigation">
    <div class="cart-holder">
       <div class="container">
           <div class="row">
               <div class="col-12">
                   <mod:global-messages htmlClasses="md-system" />
                   <c:if test="${empty cartData.entries}">
                       <mod:cart-emptied htmlClasses="hidden"/>
                   </c:if>
               </div>
               <c:choose>
                   <c:when test="${empty cartData.entries}">
                       <div class="col-12">
                           <div class="cart-holder__title">
                               <div class="base">
                                   <h1>
                                       ${sTitleEmpty}
                                   </h1>
                                   <p>
                                       ${sTextEmpty}
                                   </p>
                               </div>
                               <div class="cart-holder__title__btn">
                                   <a href="${homeUrl}" class="mat-button mat-button--action-blue">
                                       ${sContinueShopping}
                                   </a>
                               </div>
                           </div>
                       </div>
                       <div class="col-12 col-lg-9">

                            <div class="cart-holder__tabs">
                                <mod:cart-tabs/>
                            </div>

                       </div>
                       <div class="col-12 col-lg-3 cart-side-sticky">
                           <div class="cart-holder__side">
                               <mod:checkout-proceed showNextButton="false"/>
                           </div>
                       </div>
                   </c:when>
                   <c:otherwise>
                       <div class="col-12">
                           <div class="cart-holder__title cart-holder__title--full">
                               <spring:message code="cart.title" var="sTitle"/>
                               <!-- Add other skin if OCI !-->
                               <c:set var="skin" value="cart"/>
                               <sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
                                   <c:set var="skin" value="oci"/>
                               </sec:authorize>
                               <mod:checkout-title title="${sTitle}"/>
                               <p class="cart-total">
                                   ${sItemTotal}
                               </p>
                           </div>
                       </div>
                       <div class="col-12 col-lg-9">
                           <div class="cart-holder__tabs">
                               <mod:cart-tabs/>
                           </div>

                           <div class="cart-holder__list">
                               <mod:cart-list template="cart" skin="cart"/>
                           </div>
                           <c:if test="${empty EditCart or EditCart}">
                               <div class="cart-holder__toolbar">
                                   <mod:cart-toolbar template="cart" skin="cart"/>
                               </div>
                           </c:if>
                           <c:if test="${isOCI == false}">
                               <div class="cart-page-recommendations">
                                   <cms:slot var="comp" contentSlot="${slots.Recommendation}">
                                       <cms:component component="${comp}"/>
                                   </cms:slot>
                               </div>
                           </c:if>
                       </div>
                       <div class="col-12 col-lg-3 cart-side-sticky">
                           <div class="cart-holder__side">
                               <mod:cart-pricecalcbox
                                       skin="cart-page"
                                       template="cart"
                                       cartData="${cartData}"
                                       showCheckoutButton="true" href="/cart/checkout"
                                       attributes="
										${showLightBox ? 'data-show-lightbox=true' : ''}
										data-lightbox-title='${lightboxTitle}'
										data-lightbox-message='${lightboxMessage}'
										data-lightbox-show-confirm-button='false'
										data-lightbox-btn-deny='${lightboxConfirmButtonText}'
										data-lightbox-btn-conf=''
									"/>
                                <mod:cart-recalculatelayer/>

                               <c:if test="${not cartData.creditBlocked and isOCI eq false}">
                                   <div class="cart-holder__side__delivery-mode">
                                       <c:if test="${(empty cartData.b2bCustomerData.defaultDeliveryMode and user.uid ne 'anonymous') or user.uid eq 'anonymous'}">
                                           *&nbsp;<spring:message code="cart.standard.delivery" />
                                       </c:if>

                                       <c:if test="${(not empty cartData.b2bCustomerData.defaultDeliveryMode and user.uid ne 'anonymous')}">
                                           *&nbsp;<spring:message code="cart.default.delivery" />
                                       </c:if>
                                   </div>
                               </c:if>

                                <mod:checkout-proceed href="/cart/checkout" showNextButton="true"/>
                               <c:if test="${customerType eq 'B2B' or customerType eq 'B2B_KEY_ACCOUNT'}">
                               		<c:if test="${currentBaseStore.quotationsEnabled and isEShopGroup eq false and isOCI eq false}">
                                        <mod:form-request-quotes template="cart" skin="cart" />
                                    </c:if>
                                </c:if>
                            </div>
                       </div>
                   </c:otherwise>
               </c:choose>
           </div>
       </div>
    </div>
    <c:if test="${(customerType eq 'B2B' or customerType eq 'B2B_KEY_ACCOUNT') and currentBaseStore.quotationsEnabled}">
        <mod:lightbox-quotation-confirmation template="form" />
        <mod:loading-state skin="loading-state hidden" />
    </c:if>
    <c:if test="${isOCI eq false}">
        <mod:lightbox-login-required-import-tool />
    </c:if>

    <c:if test="${movMissingValue gt 0}">
        <c:choose>
            <c:when test="${not empty movDisplayMessageOnPageLoad and movDisplayMessageOnPageLoad eq true}">
                <mod:mov-pop-up htmlClasses="mod-mov-pop-up--show" attributes="${movMissingValue}" />
            </c:when>
            <c:otherwise>
                <mod:mov-pop-up attributes="${movMissingValue}" />
            </c:otherwise>
        </c:choose>
    </c:if>

    <mod:lightbox-share-email />

</views:page-default-md-full>
