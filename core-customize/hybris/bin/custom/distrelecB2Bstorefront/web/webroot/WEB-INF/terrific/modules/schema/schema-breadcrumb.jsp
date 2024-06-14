<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script type="application/ld+json">
    {
      "@context": "http://schema.org",
      "@type": "BreadcrumbList",
      "itemListElement":
        [
           <c:forEach items="${breadcrumbs}" var="breadcrumb" varStatus="status">
           <c:set var="urlLink" value="${baseUrl}${fn:startsWith(breadcrumbUrl, '/') ? '' : '/'}${breadcrumbUrl}" />
            {
              "@type": "ListItem",
              "position": ${status.index + 1},
              "item":
              {
                 "@id": "${urlLink}",
                 "name": "${breadcrumb.name}"
              }
            <c:choose>
                <c:when test="${status.last}">
                    <c:out value="}" />
                </c:when>
                <c:otherwise>
                    <c:out value="}," />
                </c:otherwise>
            </c:choose>
        </c:forEach>
        ]
    }
</script>