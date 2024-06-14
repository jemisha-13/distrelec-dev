<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:if test="${request.secure}">
	<c:url var="autocompleteUrl" value="/search/autocompleteSecure"/>
</c:if>
<c:if test="${not request.secure}">
	<c:url var="autocompleteUrl" value="/search/autocomplete"/>
</c:if>
<c:set var="searchValue" value=""/>
<%-- <c:catch var="exceptionSearchvalue">${searchPageData.freeTextSearch}</c:catch> --%>
<c:if test="${exceptionSearchvalue==null}">
	<c:if test="${searchPageData.freeTextSearch != '*' && searchPageData.freeTextSearch != ''}">
		<c:set var="searchValue" value="${searchPageData.freeTextSearch}"/>
	</c:if>
</c:if>
<spring:theme code="metahd.search.placeholder" var="sPlaceholder" />
<form:form action="/search" method="GET">
	<label class="vh" for="metahd-searchemptypage"><spring:theme code="metahd.search.label" /></label>
	<button class="btn-search" type="submit"><spring:theme code="metahd.search.label" /><i></i></button>
	<div class="input-wrapper-searchemptypage">
		<input name="q" id="metahd-searchemptypage" class="input-searchemptypage" type="search" placeholder="<spring:message code="search.no.results.searchAgain" />" value="" maxlength="255" autocomplete="off" data-typeahead-uri="${autocompleteUrl}" data-typeahead-minlength="3" />
	</div>
</form:form>