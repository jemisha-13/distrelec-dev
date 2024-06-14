<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/terrific/template" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="idKey" required="true" type="java.lang.String" %>
<%@ attribute name="name" required="false" type="java.lang.String" %>
<%@ attribute name="labelKey" required="false" type="java.lang.String" %>
<%@ attribute name="labelArguments" required="false" %>
<%@ attribute name="path" required="false" type="java.lang.String" %>
<%@ attribute name="labelCSS" required="false" type="java.lang.String" %>
<%@ attribute name="inputCSS" required="false" type="java.lang.String" %>
<%@ attribute name="value" required="false" type="java.lang.String" %>
<%@ attribute name="formGroupClass" required="false" type="java.lang.String" %>
<%@ attribute name="isChecked" required="false" type="java.lang.Boolean" %>

<div class="${not empty formGroupClass ? formGroupClass : 'form-group'} ux-checkbox">
    <input id="${idKey}" class="ux-checkbox__input ${inputCSS}" type="checkbox" name="${name}" value="${value}" ${isChecked ? 'checked' : ''}>

    <label id="${idKey}_label" class="ux-checkbox__label ${labelCSS}" for="${idKey}">
        <spring:theme code="${labelKey}" arguments="${labelArguments}" />

        <span class="ux-checkbox__state">
            <i class="fa fa-check" aria-hidden="true"></i>
        </span>
    </label>
</div>
