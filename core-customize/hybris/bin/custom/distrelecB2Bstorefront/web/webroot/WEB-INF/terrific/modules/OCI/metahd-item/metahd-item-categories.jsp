<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:theme code="metahd.search.categories.all" text="All" var="sAll" />
<div class="menuitem-wrapper">
	<div class="menuitem">
		<span class="label all"><spring:theme code="metahd.search.categories.select" text="Select Category" /></span>
		<span class="label selected-categories hidden">
			<span class="count"></span> <spring:theme code="metahd.search.categories.selected" text="Selected Categories" /> <span class="category-names hidden"></span>
		</span>
		<span class="icon-dropdown"><i></i></span>
	</div>

	<section class="flyout">
		<div class="hd"></div>
		<div class="bd">
			<input id="${sAll}" name="categories" class="checkbox js-all-categories" type="checkbox" value="${sAll}">
			<label for="${sAll}">${sAll}</label>
			<c:if test="${not empty selectedLevelOneCategories}">
				<c:forEach items="${selectedLevelOneCategories}" var="selectedLevelOneCategory">
					<c:set var="categoryName" value="${selectedLevelOneCategory.name}" />
					<c:set var="categoryCode" value="${selectedLevelOneCategory.code}" />
					<c:set var="categorySelected" value="${selectedLevelOneCategory.selected}" />
					<input id="${categoryName}" name="categories" class="checkbox js-category" type="checkbox" value="${categoryCode}" ${categorySelected ? 'checked' : ''}>
					<label for="${categoryName}">${categoryName}</label>
				</c:forEach>
			</c:if>
			<c:if test="${not empty levelOneCategories and empty selectedLevelOneCategories}">
				<c:forEach items="${levelOneCategories}" var="levelOneCategory">
					<c:set var="categoryName" value="${levelOneCategory.name}" />
					<c:set var="categoryCode" value="${levelOneCategory.code}" />
					<input id="${categoryName}" name="categories" class="checkbox js-category" type="checkbox" value="${categoryCode}">
					<label for="${categoryName}">${categoryName}</label>
				</c:forEach>
			</c:if>
		</div>
	</section>
</div>