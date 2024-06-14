<%-- Common module settings --%>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="type" description="The type of banner (warning, info, success...)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="icon" description="Banner can contain icon" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="iconCode" description="For Google Material icons, we need to pass codepoint" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="checkbox" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="checkboxClass" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="path" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="value" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="isChecked" type="java.lang.Boolean" required="false" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Specific module settings --%>
<%@ tag description="Module: banner - Templates: normal" %>

<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<%-- Module template selection --%>
<terrific:mod name="banner-ux" tag="${tag}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
    <div class="ux-banner is-${type}">
        <div class="ux-banner__icon">
            <i class="${icon}">${iconCode}</i>
        </div>

        <div class="ux-banner__text">
            <jsp:doBody />
        </div>

        <c:if test="${not empty checkbox}">
            <div class="ux-banner__check">
                <formUtil:ux-formCheckbox idKey="${checkbox}"
                                          inputCSS="${checkboxClass}"
                                          value="${value}"
                                          isChecked="${isChecked}"
                                          name="${checkbox}"
                                          path="${path}"/>
            </div>
        </c:if>
    </div>
</terrific:mod>
