<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test = "${not empty compareProducts}">
	<dt>
		Compare products
	</dt>
	
	<c:forEach items="${compareProducts}" var="product">
		<dd>
			<a href="${product.url}">
				<div>
					<c:set var="imageUrl" value="${not empty product.productImages[0].landscape_small_webp.url ? product.productImages[0].landscape_small_webp.url : product.productImages[0].landscape_small.url }"/>
					<c:set var="imageAlt" value="${not empty product.productImages[0].landscape_small_webp.altText ? product.productImages[0].landscape_small_webp.altText : product.productImages[0].landscape_small.altText}"/>
					<img src="${imageUrl}" alt="${imageAlt}" />
				</div>
				<div>
					${product.name}<br />
					<c:choose>
				        <c:when test="${not empty product.listPrice}">
				        	${product.price.formattedValue}
				        </c:when>
				        <c:otherwise>
				            ${product.price.formattedValue}
				        </c:otherwise>
				    </c:choose>
			    </div>
			</a>		
		</dd>
	</c:forEach>
</c:if>
