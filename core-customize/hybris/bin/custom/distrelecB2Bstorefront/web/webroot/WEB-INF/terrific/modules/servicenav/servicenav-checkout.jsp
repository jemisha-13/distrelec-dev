<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<div class="sticky-level-3 md-system">
    <div class="line red"></div>
    <div class="line blue"></div>
    <div class="container">
            <cms:slot var="feature" contentSlot="${slots['Disruptions']}">
                <c:if test="${fn:contains(feature['class'].name, 'DistWarningComponentModel')}">
                    <cms:component component="${feature}"/>
                </c:if>
            </cms:slot>
    </div>

</div>



