<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>

<ul>
    <c:forEach items="${categoryPageData.subCategories}" var="category">
        <c:if test="${not empty category.name && not empty category.url && !ycommerce:isCategoryPunchedout(category.code, request)}">
            <li>
                <c:url value="${category.url}" var="categoryUrl"/>
                <c:set var="categoryName" value="${fn:replace(category.name, ' ', '-')}" /> 
                <a href="${categoryUrl}" name="${wtTeaserTrackingIdLnv}.${fn:toLowerCase(categoryName)}.-">${category.name}<i></i></a>
            </li>
        </c:if>
    </c:forEach>
</ul>