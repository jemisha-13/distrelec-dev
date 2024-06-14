<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="groupClass" required="false" type="java.lang.String" %>
<%@ attribute name="labelKey" required="false" type="java.lang.String" description="If we pass 'false', then label won't be loaded in DOM, if we don't pass anything, empty label will be loaded" %>
<%@ attribute name="inputId" required="false" type="java.lang.String" %>
<%@ attribute name="inputClass" required="false" type="java.lang.String" %>
<%@ attribute name="inputType" required="false" type="java.lang.String" %>
<%@ attribute name="inputValue" required="false" type="java.lang.String" %>
<%@ attribute name="inputName" required="false" type="java.lang.String" %>
<%@ attribute name="inputAttributes" required="false" type="java.lang.String" %>
<%@ attribute name="errorGroupClass" required="false" type="java.lang.String" %>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean" %>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean" %>
<%@ attribute name="path" required="false" type="java.lang.String" %>
<%@ attribute name="readonly" required="false" type="java.lang.Boolean" %>
<%@ attribute name="maxLength" required="false" type="java.lang.Integer" %>
<%@ attribute name="placeHolderKey" required="false" type="java.lang.String" %>
<%@ attribute name="tabindex" required="false" type="java.lang.Integer" %>
<%@ attribute name="groupInfoKey" required="false" type="java.lang.String" %>
<%@ attribute name="optionalLabel" required="false" type="java.lang.Boolean" %>

<c:if test="${not empty labelKey and labelKey ne 'false'}">
    <spring:message code="${labelKey}" var="labelText"/>
</c:if>

<c:if test="${not empty placeHolderKey}">
    <spring:message code="${placeHolderKey}" var="placeHolderText" />
</c:if>

<c:if test="${not empty groupInfoKey}">
    <spring:message code="${groupInfoKey}" var="groupInfoText" />
</c:if>

<div class="ux-form-group ${groupClass}">
    <c:if test="${labelKey ne 'false'}">
        <label class="ux-form-group__label ${optionalLabel ? 'has-optional-label' : ''}" for="${inputId}">
            <span id="${inputId}_label">${not empty labelText ? labelText : ''}${mandatory ? ' *' : ''}</span>
            <c:if test="${optionalLabel}">
                <span id="${inputId}_optional-label" class="is-optional">
                    <spring:message code="register.optional.label"/>
                </span>
            </c:if>
        </label>
    </c:if>

    <div class="p-relative">
        <c:choose>
            <c:when test="${not empty path}">
                <form:input id="${inputId}"
                            path="${path}"
                            cssClass="ux-form-group__field ${inputClass}"
                            value="${inputValue}"
                            type="${not empty inputType ? inputType : 'text'}"
                            placeholder="${placeHolderText ? placeHolderText : ''}"
                            tabindex="${tabindex}"
                            readonly="${readonly}"
                            htmlEscape="true"
                            disabled="${disabled}"
                            maxlength="${maxLength}" />
            </c:when>

            <c:otherwise>
                <input id="${inputId}" class="ux-form-group__field ${inputClass}"
                       name="${inputName}"
                       value="${inputValue}"
                       placeholder="${placeHolderText ? placeHolderText : ''}"
                       maxlength="${maxLength}"
                       ${readonly ? 'readonly' : ''}
                       type="${not empty inputType ? inputType : 'text'}">
            </c:otherwise>
        </c:choose>

        <%-- Since we cannot add custom data-attributes directly on form:input, we are using this helper element which will contain those custom attrs --%>
        <c:if test="${not empty inputAttributes}">
            <span class="js-iv-field-attrs" ${inputAttributes} hidden></span>
        </c:if>

        <i class="tickItem fa fa-check"></i>
        <i class="tickItemError fa fa-times"></i>
    </div>

    <div class="ux-form-group__errors ${errorGroupClass}">
        <jsp:doBody/>
    </div>

    <c:if test="${not empty groupInfoKey}">
        <p id="${inputId}_info-txt" class="ux-form-group__info">${groupInfoText}</p>
    </c:if>
</div>
