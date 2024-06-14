<%@ page trimDirectiveWhitespaces="true" contentType="application/xml" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<?xml version="1.0" encoding="UTF-8" ?>

<spring:message code="product.image.missing" text="Image not found" var="sImageMissing"/>
<spring:message code="product.manufacturer.image.missing" text="Image not found" var="sImageMissing"/>


<element>
	<c:if test="${not empty productData}">
		<property name="id"><![CDATA[${productData.codeErpRelevant}]]></property>
	    
	    <c:set var="logoLink" value="" />
	    <c:choose>
			<c:when test="${not empty productData.distManufacturer}">
				<c:if test="${productData.distManufacturer.image.brand_logo.url ne null}">
					<c:set var="logoImageUrl" value="${baseURL}${productData.distManufacturer.image.brand_logo.url}" />
					<c:set var="logoAltText" value="${productData.distManufacturer.name == null ? sImageMissing : productData.distManufacturer.name}"/>
				</c:if>
				<c:set var="logoLink" value="${baseURL}/${productData.distManufacturer.urlId}" />
			</c:when>
			<c:when test="${manufacturerLogoUrl ne null}">
				<c:set var="logoImageUrl" value="${baseUrl}${manufacturerLogoUrl}" />
				<c:set var="logoAltText" value="${manufacturerLogoAltText == null ? sImageMissing : manufacturerLogoAltText}"/>
			</c:when>
			<c:otherwise>
				<c:set var="logoImageUrl" value="" />
				<c:set var="logoAltText" value="${sImageMissing}"/>
			</c:otherwise>
		</c:choose>
	   
	   	<property name="logoAltText"><![CDATA[${logoAltText}]]></property>
	    <property name="logoImageUrl"><![CDATA[${logoImageUrl}]]></property>
	    <property name="logoLink"><![CDATA[${logoLink}]]></property>
	    
	    <property name="specialOffer"><![CDATA[]]></property>
	    <property name="imageUrl"><![CDATA[${imageURL}]]></property>
	    <property name="manufacturer"><![CDATA[${not empty productData.distManufacturer ? productData.distManufacturer.name : ''}]]></property>
	    <property name="name"><![CDATA[${productData.name}]]></property>
	    <property name="currency"><![CDATA[${productData.price.currencyIso}]]></property>
	    <property name="currentPricePrefix"><![CDATA[${productData.price.currencyIso}]]></property>
	    <property name="currentPrice"><![CDATA[]]></property>
	    <property name="oldPricePrefix"><![CDATA[${productData.price.currencyIso}]]></property>
	    <property name="oldPrice"><![CDATA[]]></property>
	    
	    <property name="linkText"><![CDATA[<spring:message code="optivo.product.goto" />]]></property>
	    <property name="linkUrl"><![CDATA[${baseURL}${productData.url}]]></property>
	    <property name="description1Property"><![CDATA[<spring:message code="optivo.product.articlenr" />]]></property>
	    <property name="description1Value"><![CDATA[${productData.codeErpRelevant}]]></property>

	    <c:set var="text" value="" />
	    <c:choose>
	    	<c:when test="${not empty productData.productInformation}" >
				<c:if test="${not empty productData.productInformation.familyDescription || not empty productData.productInformation.familyDescriptionBullets || not empty productData.productInformation.seriesDescription || not empty productData.productInformation.seriesDescriptionBullets}">
					<c:if test="${not empty productData.productInformation.familyDescription || not empty productData.productInformation.familyDescriptionBullets}">
						<c:set var="text" value="${productData.productInformation.familyDescription}" />							
						<c:forEach items="${productData.productInformation.familyDescriptionBullets}" var="bullet">
							<c:set var="text" value="${text} ${bullet}" />
						</c:forEach>
					</c:if>
					<c:if test="${not empty productData.productInformation.seriesDescription || not empty productData.productInformation.seriesDescriptionBullets}">
						<c:set var="text" value="${productData.productInformation.seriesDescription}" />
						<c:forEach items="${productData.productInformation.seriesDescriptionBullets}" var="bullet">
							<c:set var="text" value="${text} ${bullet}" />
						</c:forEach>
					</c:if>
				</c:if>

				<%-- ARTICLE INFORMATION --%>
				<c:if test="${not empty productData.productInformation.articleDescription }">
					<c:forEach items="${productData.productInformation.articleDescriptionBullets}" var="bullet">
						<c:set var="text" value="${text} ${bullet}" />
					</c:forEach>
				</c:if>
	    	</c:when>
	    	<c:otherwise>
	    		<c:set var="text" value="" />
	    	</c:otherwise>
	    </c:choose>
	    
	    <c:set var="sectionCount" value="0" />
		<c:set var="visibilityCount" value="0" />
		<c:set var="totalCount" value="0" />
	    <c:set var="counter" value="2" />
	    
	    <c:forEach items="${productData.classifications}" var="classification">
			<c:forEach items="${classification.features}" var="feature">
				<c:choose>
					<c:when test="${feature.visibility eq 'a_visibility' or visibilityCount < 2}">
						<c:set var="visibilityCount" value="${ visibilityCount + 1 }" />
					</c:when>
				</c:choose>
				<c:if test="${visibilityCount == totalCount}">
					<c:set var="sectionCount" value="0" />
				</c:if>
				<property name="description${counter}Property"><![CDATA[]]></property>
				<c:set var="featureValue" value="" />
				<c:forEach items="${feature.featureValues}" var="value" varStatus="status">
					<c:set var="featureValue" value="${featureValue} ${value.value }" />
				</c:forEach>
				<property name="description${counter}Value"><![CDATA[]]></property>
				<c:set var="sectionCount" value="${ sectionCount + 1 }" />
				<c:set var="totalCount" value="${ totalCount + 1 }" />
				<c:set var="counter" value="${ counter + 1 }" />
			</c:forEach>
		</c:forEach>
	</c:if>
	<property name="text"><![CDATA[${text}]]></property>
</element>