<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="arrow_prev <c:if test="${not empty product.prevUrl}">active</c:if>">
    <span data-href="${product.prevUrl}<c:if test="${not empty filterURL}">&filterURL=${filterURL}</c:if>" class="btn btn-prev"></span>
</div>
<div class="arrow_next <c:if test="${not empty product.nextUrl}">active</c:if>">
    <span data-href="${product.nextUrl}<c:if test="${not empty filterURL}">&filterURL=${filterURL}</c:if>"  class="btn btn-next"></span>
</div>
