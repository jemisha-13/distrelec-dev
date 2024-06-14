<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>


<views:page-checkout pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-checkout" >

    <c:if test="${not empty message}">
        <spring:theme code="${message}"/>
    </c:if>           

	<mod:global-messages/>		

	<mod:eproc-success template="cxml" skin="cxml"/>

</views:page-checkout>

