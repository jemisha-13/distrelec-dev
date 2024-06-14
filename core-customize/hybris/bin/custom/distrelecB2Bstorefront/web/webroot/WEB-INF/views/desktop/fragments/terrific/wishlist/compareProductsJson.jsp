<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<json:object>
	<json:object name="compareProductsData">
		<json:array name="products" items="${metaCompareProducts}" var="product">
			<json:object>
				<json:property name="code" value="${product.code}"/>
				<json:property name="name" value="${product.name}"/>
				<json:property name="price" value="${product.price.value}"/>
				<json:property name="priceLocal">
					<format:price format="simple" priceData="${product.price}"/>
				</json:property>
				<c:set var="primaryImage" value="${not empty product.productImages[0] ? product.productImages[0].landscape_small : ''}" />
				<c:choose>
					<c:when test="${not empty primaryImage}">
						<json:property name="thumbUrl" value="${primaryImage.url}" />
						<c:set var="primaryImageWebp" value="${not empty images.landscape_small_webp ? images.landscape_small_webp : primaryImage}" />
						<json:property name="thumbUrlWebp" value="${primaryImageWebp.url}" />
					</c:when>
					<c:otherwise>
						<json:property name="thumbUrl" value="/_ui/all/media/img/missing_landscape_small.png" />
						<json:property name="thumbUrlWebp" value="/_ui/all/media/img/missing_landscape_small.png" />
					</c:otherwise>
				</c:choose>
				<json:property name="productUrl">
					<c:url value="${product.url}" />
				</json:property>				
			</json:object>
		</json:array>
	</json:object>
</json:object>
