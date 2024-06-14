<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<article class="card__item card__item-OCI">
    <div class="card__item__img">
<c:choose>
    <c:when test="${fn:contains(wtPageId, 'productDetails') || fn:contains(wtPageId, 'homepage2018')}">
        <img src="data:image/png;base64,R0lGODlhAQABAAD/ACwAAAAAAQABAAACADs=" alt="${headline}" data-src="${media.URL}" width="380" height="89" class="img-defer"/>
    </c:when>
    <c:otherwise>
        <img alt="${headline}" src="${media.URL}" width="380" height="89" />
    </c:otherwise>
</c:choose>
    </div>
    <div class="card__item__info">
        <h2 class="card__item__title">${headline}</h2>
        <p class="card__item__description">${content}</p>
    </div>
</article>
