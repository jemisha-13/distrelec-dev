<%@ page trimDirectiveWhitespaces="true" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:message code="oci.page.title" var="sPageTitle"/>

<views:page-checkout pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-checkout skin-layout-oci-success" >

    <c:if test="${not empty message}">
        <spring:theme code="${message}"/>
    </c:if>           

	<mod:global-messages/>

	<mod:eproc-success/>

</views:page-checkout>
