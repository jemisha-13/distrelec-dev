<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         trimDirectiveWhitespaces="false" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:eval expression="new java.util.Locale('en')" var="enLocale" />

<section class="home-featured-products">

    <div class="container">
        <div class="row">
            <div class="col-12">
                <h2 class="heading">
                    <c:choose>
                        <c:when test="${empty sectionTitle}">
                            <spring:message code="featuredProducts.title" />
                        </c:when>
                        <c:otherwise>
                            ${sectionTitle}
                        </c:otherwise>
                    </c:choose>
                </h2>
            </div>

                <c:forEach var="product" items="${products}" varStatus="status">

                <div class="product__tile" data-productcode="${product.code}">

                    <div class="product__tile__item">

                        <c:choose>
                            <c:when test="${product.catPlusItem}">
                                <div class="product__tile__item__service service-plus-wrap">
                                    <mod:catplus-logo catplusLogoAltText="catplus logo" catplusLogoUrl="/Web/WebShopImages/catplus/catplus-logo.gif" />
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="product__tile__item__manufacturer manufacturer-wrap">
                                    <mod:product-manufacturer distManufacturer="${product.distManufacturer}" showExternalLink="false" showLink="true" />
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <a href="${product.url}" data-aabuttonpos="${status.count}"
                                <c:forEach var="dataAttributeEntry" items="${dataAttributes}">
                                    <c:choose>
                                        <c:when test="${dataAttributeEntry.key == 'aaLinkText'}">
                                            data-aaLinkText="Image"
                                        </c:when>
                                        <c:otherwise>
                                            data-${dataAttributeEntry.key}="${dataAttributeEntry.value}"
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach> >
                            <c:set var="portraitSmallJpg" value="${product.productImages[0].portrait_small.url}"/>
                            <c:set var="portraitSmallWebP" value="${not empty product.productImages[0].portrait_small_webp.url ? product.productImages[0].portrait_small_webp.url : portraitSmallJpg}"/>
                            <picture>
                                <source sourceset="${portraitSmallWebP}">
                                <img alt="${product.name}" class="product__tile__item__image ${status.count == 1 ? "img_defer" : ""}" data-src="${portraitSmallJpg}" width="90" height="160">
                            </picture>
                            <c:if test="${(not empty product.activePromotionLabels) && (product.salesStatus ne 40) && (product.salesStatus ne 41)}">
                                <div class="product__tile__item__promo-label">
                                    <mod:product-label promoLabel="${product.activePromotionLabels[0]}" />
                                </div>
                            </c:if>
                        </a>

                        <h3 class="col-12 product__tile__item__title" title='<c:if test='${!empty product.typeName}'>${product.typeName} - </c:if>${product.name}<c:if test='${!empty product.distManufacturer}'> - ${product.distManufacturer.name}</c:if>'>

                            <c:if test='${!empty product.typeName}'>
                                <c:set var = "typeName" value = '${product.typeName} -'/>
                            </c:if>
                            <c:if test='${!empty product.name}'>
                                <c:set var = "name" value = '${product.name}'/>
                                <c:set var = "unlocalizedName" value='${data2modelMap.get(product).getName(enLocale)}' />
                            </c:if>
                            <c:if test='${!empty product.distManufacturer}'>
                                <c:set var = "manufacturerName" value = "- ${product.distManufacturer.name}"/>
                            </c:if>

                            <c:set var = "featureProductTitle" value = ' ${typeName} ${name} ${manufacturerName} '/>
                            <c:set var = "unlocalizedFeatureProductTitle" value=' ${typeName} ${unlocalizedName} ${manufacturerName}' />
                            <c:set var = "maxLength" value = '48'/>

                            <a href='${product.url}' data-aabuttonpos='${status.count}'
                                    <c:forEach var="dataAttributeEntry" items='${dataAttributes}'>
                                        <c:choose>
                                            <c:when test='${dataAttributeEntry.key == "aaLinkText"}'>
                                                data-aaLinkText='${unlocalizedFeatureProductTitle.trim()}'
                                            </c:when>
                                            <c:otherwise>
                                                data-${dataAttributeEntry.key}='${dataAttributeEntry.value}'
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach> >

                                <c:choose>
                                    <c:when test = '${fn:length(featureProductTitle) <= maxLength}'>
                                        ${featureProductTitle}
                                    </c:when>
                                    <c:otherwise>
                                        ${fn:substring(featureProductTitle, 0, maxLength)}...
                                    </c:otherwise>
                                </c:choose>

                            </a>

                        </h3>
                        <span class="col-12 product__tile__item__price">${product.price.formattedValue}</span>
                        <div class="col-12 product__tile__item__cta-wrapper">
                            <a class="product__tile__item__cta mat-button mat-button__solid--action-green"
                               href="${product.url}" data-aabuttonpos="${status.count}"
                                    <c:forEach var="dataAttributeEntry" items="${dataAttributes}">
                                        data-${dataAttributeEntry.key}="${dataAttributeEntry.value}"
                                    </c:forEach>
                            > ${linkText} <i class="fas fa-angle-right"></i> </a>
                        </div>
                    </div>
                </div>

            </c:forEach>
        </div>
    </div>


</section>