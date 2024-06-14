<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var = "productMessageKey" value = "product.message.status.${statusCode}" />
<spring:message code="${productMessageKey}" var="statusText" arguments="${product.name}�${product.code}" argumentSeparator="�" text="" />

<c:if test="${ statusCode eq 20 }">
	<c:set var = "isVisible" value = "" />
</c:if>

<div class="${isVisible} statusMessageContainer status-${statusCode}"  >
	<div class="eolText">
		<c:choose>
			<c:when test="${not empty replacementProductList}">
				<c:out value="${statusText}" /><span class="eolText__btn"><spring:theme
					code="product.eol.please.check.here" text="here" /><i class="fa fa-angle-right" aria-hidden="true"></i></span>
			</c:when>
			<c:when test="${not empty replacementProductLink}">
				<c:out value="${statusText}" /><c:if test="${statusCode != 20 && statusCode != 21 && statusCode != 53 && statusCode != 52 && statusCode != 50 && statusCode != 51}"><a
					class="eolLink" href="${replacementProductLink}"><spring:theme
					code="product.eol.please.check.here" text="here" /><i class="fa fa-angle-right" aria-hidden="true"></i></a></c:if>
			</c:when>
			<c:otherwise>
				<c:out value="${statusText}" />
			</c:otherwise>
		</c:choose>
	</div>
</div>
