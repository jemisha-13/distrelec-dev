<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="imageUrl" required="true" type="java.lang.String" %>
<%@ attribute name="imageAlt" required="false" type="java.lang.String" %>
<%@ attribute name="imageWidth" required="false" type="java.lang.String" %>
<%@ attribute name="productName" required="false" type="java.lang.String" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:message code="cart.image.missing" text="Image not found" var="sImageMissing"/>
<img id="${productName}_image" alt="${imageAlt eq null ? (imageUrl eq null ? sImageMissing : productName) : imageAlt}" src="${imageUrl eq null ?  '/_ui/all/media/img/missing_portrait_small.png' : imageUrl}" width="${imageWidth}">
