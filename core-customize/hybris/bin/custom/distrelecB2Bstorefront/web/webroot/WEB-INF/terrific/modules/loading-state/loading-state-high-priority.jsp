<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:url value="${commonResourcePath}/images/loader.gif" var="spinnerUrl" />

<div class="ajax-product-loader ajax-product-loader--high-priority d-none">
    <div class="background-overlay"></div>
    <div class="message-wrapper">
        <div class="loading-message">
            <img id="spinner" src="${spinnerUrl}" alt="spinner" class="loading-message__icon"/>
            <c:if test="${not empty loadingStateMessage}">
                <p class="loading-message__text">${loadingStateMessage}</p>
            </c:if>
        </div>
    </div>
</div>
