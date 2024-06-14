<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<json:object>
	<json:object name="searchResult">
		<json:array name="suggestions" var="suggestion" items="${productSuggestions}">
			<json:object>
				<json:property name="code" value="${suggestion.id}" />
				<json:property name="codeErpRelevant" value="${suggestion.codeErpRelevant}" />
				<json:property name="name" value="${suggestion.name}" />
				<json:property name="imageUrl" value="${suggestion.img_url}" />
				<json:property name="description" value="${suggestion.typeName}" />
				<json:property name="orderQuantityMin" value="${suggestion.items_min}" />
				<json:property name="orderQuantityStep" value="${suggestion.items_step}" />
				<json:property name="formattedPrice" value="${suggestion.price}" />
				<json:property name="formattedPriceValue" value="${suggestion.formattedPriceValue}" />
				<json:property name="currencyIso" value="${suggestion.currencyIso}" />
			</json:object>
		</json:array>
	</json:object>
</json:object>
