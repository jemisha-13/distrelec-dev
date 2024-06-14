<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="items" required="true" type="java.util.Collection" %>
<%@ attribute name="itemValue" required="false" type="java.lang.String" %>
<%@ attribute name="itemLabel" required="false" type="java.lang.String" %>
<%@ attribute name="groupClass" required="false" type="java.lang.String" %>
<%@ attribute name="labelKey" required="false" type="java.lang.String" description="If we pass 'false', then label won't be loaded in DOM, if we don't pass anything, empty label will be loaded" %>
<%@ attribute name="selectId" required="false" type="java.lang.String" %>
<%@ attribute name="selectClass" required="false" type="java.lang.String" %>
<%@ attribute name="selectName" required="false" type="java.lang.String" %>
<%@ attribute name="selectAttributes" required="false" type="java.lang.String" %>
<%@ attribute name="errorGroupClass" required="false" type="java.lang.String" %>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean" %>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean" %>
<%@ attribute name="path" required="false" type="java.lang.String" %>
<%@ attribute name="placeHolderKey" required="false" type="java.lang.String" %>
<%@ attribute name="tabindex" required="false" type="java.lang.Integer" %>
<%@ attribute name="groupInfoKey" required="false" type="java.lang.String" %>
<%@ attribute name="skipBlank" required="false" type="java.lang.Boolean" %>
<%@ attribute name="skipBlankSelectable" required="false" type="java.lang.Boolean" description="True if the top skipBlank option should be selectabele" %>
<%@ attribute name="skipBlankMessageKey" required="false" type="java.lang.String" %>
<%@ attribute name="selectedValue" required="false" type="java.lang.String" %>
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
        <label class="ux-form-group__label ${optionalLabel ? 'has-optional-label' : ''}" for="${selectId}">
            <span id="${selectId}_label">${not empty labelText ? labelText : ''}${mandatory ? ' *' : ''}</span>
            <c:if test="${optionalLabel}">
                <span id="${selectId}_optional-label" class="is-optional">
                    <spring:message code="register.optional.label"/>
                </span>
            </c:if>
        </label>
    </c:if>

    <div class="p-relative">
        <c:choose>
            <c:when test="${not empty path}">
                <form:select id="${selectId}"
                             path="${path}"
                             cssClass="ux-form-group__field ${selectClass}"
                             disabled="${disabled}"
                             tabindex="${tabindex}">
                    <c:if test="${skipBlank == null || skipBlank == false}">
                        <option value="" ${skipBlankSelectable == null || skipBlankSelectable == false ? 'disabled="disabled"' : ''} selected="${selectedValue == null ? 'selected' : ''}"><spring:theme code='${skipBlankMessageKey}'/></option>
                    </c:if>

                    <%-- manual forEach is used instead of form:options tag, because dynamic title is not supported by the form:options tag --%>
                    <%-- some selects do not have a itemValue property named 'code', thats why we have the possibility to provide a dynamic itemValue property name--%>
                    <%-- some selects do not have a itemLabel property named 'name', thats why we have the possibility to provide a dynamic itemLabel property name--%>

                    <%-- if selectedValue is set, we generate the options manually, since form:option tag only works with a path variable and is not taking a selected value --%>
                    <c:forEach items="${items}" var="item" varStatus="status">
                        <c:set var="optionValue" value="${not empty itemValue ? item[itemValue] : item['code']}" />
                        <c:set var="optionLabel" value="${not empty itemLabel ? item[itemLabel] :item['name']}" />

                        <c:choose>
                            <c:when test="${not empty selectedValue}">
                                <option value="${optionValue}" title="${optionLabel}" ${selectedValue == optionValue ? 'selected="selected"' : ''}>
                                        ${optionLabel}
                                </option>
                            </c:when>
                            <c:otherwise>
                                <form:option value="${optionValue}" label="${optionLabel}" title="${optionLabel}"  />
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </form:select>
            </c:when>

            <c:otherwise>
                <select id="${selectId}" class="ux-form-group__field ${selectClass}" name="${selectName}">
                    <c:if test="${skipBlank == null || skipBlank == false}">
                        <option value="" ${skipBlankSelectable == null || skipBlankSelectable == false ? 'disabled="disabled"' : ''} selected="${selectedValue == null ? 'selected' : ''}"><spring:theme code='${skipBlankMessageKey}'/></option>
                    </c:if>

                        <%-- manual forEach is used instead of form:options tag, because dynamic title is not supported by the form:options tag --%>
                        <%-- some selects do not have a itemValue property named 'code', thats why we have the possibility to provide a dynamic itemValue property name--%>
                        <%-- some selects do not have a itemLabel property named 'name', thats why we have the possibility to provide a dynamic itemLabel property name--%>

                        <%-- if selectedValue is set, we generate the options manually, since form:option tag only works with a path variable and is not taking a selected value --%>
                    <c:forEach items="${items}" var="item" varStatus="status">
                        <c:set var="optionValue" value="${not empty itemValue ? item[itemValue] : item['code']}" />
                        <c:set var="optionLabel" value="${not empty itemLabel ? item[itemLabel] :item['name']}" />

                        <c:choose>
                            <c:when test="${not empty selectedValue}">
                                <option value="${optionValue}" title="${optionLabel}" ${selectedValue == optionValue ? 'selected="selected"' : ''}>
                                        ${optionLabel}
                                </option>
                            </c:when>
                            <c:otherwise>
                                <option value="${optionValue}" title="${optionLabel}">${optionLabel}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </c:otherwise>
        </c:choose>

        <%-- Since we cannot add custom data-attributes directly on form:select, we are using this helper element which will contain those custom attrs --%>
        <c:if test="${not empty selectAttributes}">
            <span class="js-iv-field-attrs" ${selectAttributes} hidden></span>
        </c:if>

        <i class="tickItem fa fa-check"></i>
        <i class="tickItemError fa fa-times"></i>
    </div>

    <div class="ux-form-group__errors ${errorGroupClass}">
        <jsp:doBody/>
    </div>

    <c:if test="${not empty groupInfoKey}">
        <p id="${selectId}_info-txt" class="ux-form-group__info">${groupInfoText}</p>
    </c:if>
</div>
