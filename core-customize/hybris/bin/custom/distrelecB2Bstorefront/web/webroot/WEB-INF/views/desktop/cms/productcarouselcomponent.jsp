<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>


<c:if test="${not empty productData}">

<div class="scroller vertical">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h2>${title}</h2>
	</div>
	<ul class="carousel jcarousel-skin">
		<c:forEach items="${productData}" var="product">

			<c:url value="${product.url}/quickView" var="productQuickViewUrl"/>
			<li>
				<a href="${productQuickViewUrl}">
					<span>
						<product:productPrimaryImage product="${product}" format="thumbnail"/>
					</span>
					<h3>
							${product.name}
					</h3>
					<p>
						<format:fromPrice priceData="${product.price}"/>
					</p>
				</a>
			</li>
		</c:forEach>
	</ul>
</div>
</c:if>
