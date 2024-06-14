<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${not empty level3node.children}">
    <ul class="level-1-wrapper">
        <c:forEach items="${level3node.children}" var="level4node">
            <c:set var="level4entry" value="${level4node.entries[0]}" />
            <li class="li-mouseover main_li level4 nav-hover">
            <div class="level_1">
                    <c:choose>
                        <c:when test="${level4entry.item.itemtype eq 'ContentPage'}">
                            <c:url value="${ycommerce:contentPageUrl(level4entry.item, request)}" var="level4url" />
                        </c:when>
                        <c:when test="${level4entry.item.itemtype eq 'CMSLinkComponent'}">
                            <c:url value="${ycommerce:cmsLinkComponentUrl(level4entry.item, request)}" var="level4url" />
                        </c:when>
                        <c:when test="${level4entry.item.itemtype eq 'Category'}">
                            <c:url value="${ycommerce:categoryUrl(level4entry.item, request)}" var="level4url" />
                        </c:when>
                        <c:otherwise>
                            <c:url value="#" var="level4url" />
                        </c:otherwise>
                    </c:choose>
                    <c:set var="titleValueWt" value="${fn:replace(level3node.title, ' ', '-')}" />
                    <c:set var="titleValueWtLower" value="${fn:toLowerCase(titleValueWt)}" />
                    <a title="${level4node.title}" class="link_l4${level4url == '#' ? ' no-click' : ''}" href="${level4url}" data-aainteraction='navigation' data-location='category nav' data-parent-link='${fn:toLowerCase(level3node.getTitle(enLocale))}' data-link-value='${fn:toLowerCase(level4node.getTitle(enLocale))}'>
                        <span title="${level4node.title}" class="level1titleClass text ellipsis">${level4node.title}</span>
                    </a>
            </div>
            </li>
        </c:forEach>
    </ul>
</c:if>