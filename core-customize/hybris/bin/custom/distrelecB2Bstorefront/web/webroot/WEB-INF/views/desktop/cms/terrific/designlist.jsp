<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>

<article id="distcomp-13" class="container bannner bannner-component bannner-component--thirteen   ">
    <div class="bannner__content ">
		<c:forEach items="${listComponents}" var="component" varStatus="loop">
            <c:if test="${not empty component.content}">
                <div class="bannner__content--description border-bottom">
                    ${component.content}
                </div>
            </c:if>
        </c:forEach>
    </div>
</article>