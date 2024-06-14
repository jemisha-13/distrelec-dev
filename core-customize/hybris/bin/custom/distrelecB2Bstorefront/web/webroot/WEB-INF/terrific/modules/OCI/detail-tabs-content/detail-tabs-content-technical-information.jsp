<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
 
<%-- check if there are searchable products to set or hide title and search-field --%>
<c:set var="searchable" value="false" />
<spring:message code="product.tabs.search.similar.products.Maintitle" var="sSimilarTitle" />
<spring:message code="product.tabs.search.similar.products.tagLine" var="sSimilarTagLine" />


 <div class="validate-holder">
	 <table class="validate-checkbox-group">
		 <tbody>
		 <c:forEach items="${product.classifications}" var="classification">
			 <c:forEach items="${classification.features}" var="feature">
				 <c:if test="${feature.searchable and (not empty feature.featureValues) && (not empty feature.featureValues[0].value) }">
					 <!-- Set the variable 'searchable' to true -->
					 <c:set var="searchable" value="true" />
					 <tr>
						 <td class="td1" valign="top">
							 <input id="${feature.name}" type="checkbox" class="feature-checkbox" name="${feature.name}" />
							 <label class="ellipsis" for="${feature.name}">${feature.name}</label>
						 </td>
						 <td class="td2" valign="top">
							 <c:forEach items="${feature.featureValuesWithUnit}" var="entry" varStatus="status">
								 <span class="feature-value">${entry.key.value}</span>
								 <c:choose>
									 <c:when test="${feature.range}">
										 <span class="feature-unit">${not status.last ? '-' : entry.value.name}</span>
									 </c:when>
									 <c:otherwise>
										 <span class="feature-unit">${entry.value.name}</span>
										 ${not status.last ? '<br/>' : ''}
									 </c:otherwise>
								 </c:choose>
							 </c:forEach>
						 </td>
					 </tr>
				 </c:if>
			 </c:forEach>
		 </c:forEach>
		 </tbody>
	 </table>


	 <div class="searchsimilar ${!searchable ? 'hidden' : ''}">
		 <p>${sSimilarTagLine}</p>
		 <div class="searchsimilar__holder">
			 <a class="mat-button btn-search" data-search-url="${similarProductBaseUrl}" href="#"><i class="fa fa-search" aria-hidden="true"></i> <spring:message code="product.tabs.search.similar.products.title" /></a>
			 <div class="btn-info"><i></i></div>
		 </div>
	 </div>
 </div>

<script id="tmpl-similarsearch-error-empty" type="text/template">
	<spring:message code="product.tabs.search.similar.products.checkbox-error" text="Please select at least one of the filters below" />
</script>


<c:forEach items="${product.classifications}" var="classification">
	<c:forEach items="${classification.features}" var="feature">
		<c:if test="${(not feature.searchable) and (not empty feature.featureValues) && (not empty feature.featureValues[0].value) }">
			<div class="item">
				<div class="item__element">
					<label class="no-checkbox ellipsis">${feature.name}:</label>
				</div>
				<div class="item__element">
					<c:forEach items="${feature.featureValues}" var="value" varStatus="status">
						<span class="feature-value">${value.value}</span>
						<c:choose>
							<c:when test="${feature.range}">
								<span class="feature-unit">${not status.last ? '-' : feature.featureUnit.name}</span>
							</c:when>
							<c:otherwise>
								<span class="feature-unit">${feature.featureUnit.name}</span>
								${not status.last ? '<br/>' : ''}
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</div>
			</div>
		</c:if>
	</c:forEach>
</c:forEach>
