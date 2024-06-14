<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="e" uri="https://www.owasp.org/index.php/OWASP_Java_Encoder_Project" %>

<spring:eval expression="@configurationService.configuration.getString('factfinder.json.suggest.url')" var="ffsuggestUrl" scope="application" />
<c:set var="searchValue" value=""/>
<%-- <c:catch var="exceptionSearchvalue">${searchPageData.freeTextSearch}</c:catch> --%>

<c:if test="${not empty ffsearchPageData}">
	<c:set var="searchPageData" value="${ffsearchPageData}"/>
</c:if>
<c:if test="${exceptionSearchvalue==null}">
	<c:if test="${searchPageData.freeTextSearch != '*' && searchPageData.freeTextSearch != ''}">
		<c:set var="searchValue">
			<e:forHtml value="${searchPageData.freeTextSearch}"/>
		</c:set>
	</c:if>
</c:if>
<c:if test="${not empty removetitle and removetitle}">
	<c:set var="searchValue" value=""/>
</c:if>
<c:set var="val"><spring:message code="metahd.search.placeholder-new"/></c:set>
<input id="placeholder-value" type="hidden" value="${val}"/>
<c:set var="valEmpty"><spring:message code="metahd.search.placeholder.empty"/></c:set>
<input id="placeholder-value-empty" type="hidden" value="${valEmpty}"/>

<spring:message code="metahd.search.placeholder-new" var="sPlaceholder" />
<spring:message code="metahd.search.placeholder.empty" var="sPlaceholderEmpty" />
<spring:message code="metahd.search.allCategory" var="sAllCategory" />

<form:form action="/search" class="searchForm" name="searchFormName" method="GET">
	<label class="vh" for="metahd-search"><spring:theme code="metahd.search.label" /></label>
	<button class="btn-search" type="submit"><i class="fa fa-search" aria-hidden="true"></i></button>
	<div class="input-wrapper">
		<input name="q" id="metahd-search" class="input-search" type="text" placeholder="${sPlaceholder}" value="${searchValue}" maxlength="255" autocomplete="off" data-typeahead-uri="${ffsuggestUrl}" data-typeahead-minlength="2" data-typeahead-channel="${ffsearchChannel}" />
		<div class="select-holder">
			<select name="filter_categoryCodePathROOT" class="metahd-select">
				<option value="">${sAllCategory}</option>
				<c:forEach items="${topCategories}" var="l1_cat" varStatus="status">
					<option value="${l1_cat.code}">
							${l1_cat.name}
					</option>
				</c:forEach>
			</select>
			<span class="select-holder__text ellipsis">${sAllCategory}</span>
		</div>
	</div>
	<%-- send selected category values to BE, values will be added by js. Empty inputs will be disabled from submit transmission. --%>
	<input type="hidden" class="selected-cat-field" value="${filter_categoryCodePathROOT}"/>
</form:form>