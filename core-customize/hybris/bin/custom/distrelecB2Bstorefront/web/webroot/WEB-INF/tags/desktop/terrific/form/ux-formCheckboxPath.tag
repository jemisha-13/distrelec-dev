<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="idKey" required="false" type="java.lang.String" %>
<%@ attribute name="name" required="false" type="java.lang.String" %>
<%@ attribute name="labelKey" required="false" type="java.lang.String" %>
<%@ attribute name="labelArguments" required="false" %>
<%@ attribute name="path" required="false" type="java.lang.String" %>
<%@ attribute name="labelCSS" required="false" type="java.lang.String" %>
<%@ attribute name="inputCSS" required="false" type="java.lang.String" %>
<%@ attribute name="inputName" required="false" type="java.lang.String" %>
<%@ attribute name="formGroupClass" required="false" type="java.lang.String" %>
<%@ attribute name="isChecked" required="false" type="java.lang.Boolean" %>

<div class="${not empty formGroupClass ? formGroupClass : 'form-group'} ux-checkbox">
    <c:choose>
        <c:when test="${not empty path}">
            <form:checkbox id="${idKey}" path="${path}" cssClass="ux-checkbox__input ${inputCSS}" />
        </c:when>

        <c:otherwise>
            <c:choose>
                <c:when test="${isChecked}">
                    <input id="${idKey}"
                           class="ux-checkbox__input ${inputCSS}"
                           name="${inputName}"
                           checked
                           type="checkbox">
                </c:when>
                <c:otherwise>
                    <input id="${idKey}"
                           class="ux-checkbox__input ${inputCSS}"
                           name="${inputName}"
                           type="checkbox">
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>

    <label id="${idKey}_label" class="ux-checkbox__label ${labelCSS}" for="${idKey}">
        <spring:theme code="${labelKey}" arguments="${labelArguments}" />

        <span class="ux-checkbox__state">
            <i class="fa fa-check" aria-hidden="true"></i>
        </span>
    </label>
</div>
