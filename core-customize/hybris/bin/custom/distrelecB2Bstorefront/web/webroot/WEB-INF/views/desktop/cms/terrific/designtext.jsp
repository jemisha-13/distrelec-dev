<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>

<c:if test="${not empty content}">
    <article id="distcomp-19" class="container bannner bannner-component bannner-component--ten ${htmlClasses}">
        <div class="bannner__content">
            <div class="bannner__content--description">
                ${content}
            </div>
        </div>
    </article>
</c:if>
