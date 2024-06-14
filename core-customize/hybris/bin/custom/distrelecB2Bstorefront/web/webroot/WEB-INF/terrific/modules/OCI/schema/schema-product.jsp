<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="req" value="${pageContext.request}" />
<c:set var="baseUrl" value="${fn:replace(req.requestURL, req.requestURI, req.contextPath)}" />
<c:set var="imageUrl" value="${not empty product.productImages[0].landscape_medium_webp.url ? product.productImages[0].landscape_medium_webp.url : product.productImages[0].landscape_medium.url}"/>
<c:set var="productUrl" value="${fn:split(product.url, '?')}" />

<c:choose>
    <c:when test="${product.stock.stockLevelStatus == 'inStock'}">
        <c:set var="availability" value="http://schema.org/InStock" />
    </c:when>
    <c:otherwise>
        <c:set var="availability" value="http://schema.org/OutOfStock" />
    </c:otherwise>
</c:choose>

<c:if test="${not empty product.productInformation.articleDescription}">
    <c:forEach items="${product.productInformation.articleDescription}" var="paragraph">
        <c:set var="description" value="${fn:escapeXml(paragraph)}" />
    </c:forEach>
</c:if>

<c:set var="productName" value="${fn:escapeXml(product.name)}" />

<script type="application/ld+json">
    {
      "@context": "http://schema.org",
      "@type": "Product",
      "name": "${productName}",
      "image": "${baseUrl} ${imageUrl}",
      "description": "${description}",
      "sku": "${product.codeErpRelevant}",
      "mpn": "${product.typeName}",
      "itemCondition":"NewCondition",
      "brand": "${product.distManufacturer.name}",
      "offers": [
        <c:forEach items="${product.volumePricesMap}" var="entry" end="6" varStatus="varStatus">
          {
            "@type": "Offer",
            "priceCurrency": "${product.price.currencyIso}",
            "price": "${entry.value.list.value}",
            "availability": "http://schema.org/InStock",
            "url": "${productUrl}",
            "eligibleQuantity": {
              "@type": "QuantitativeValue",
              "minValue": "${entry.key}"
            }
          }<c:if test="${not varStatus.last}">,</c:if>
        </c:forEach>
      ]
    }
</script>