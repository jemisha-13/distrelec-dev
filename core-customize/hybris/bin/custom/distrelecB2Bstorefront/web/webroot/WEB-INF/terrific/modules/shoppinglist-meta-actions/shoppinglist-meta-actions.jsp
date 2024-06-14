<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:theme code="shoppinglist.metaAction.addNewListLabel" text="Add new Shopping List" var="addListLabel" />
<spring:theme code="shoppinglist.metaAction.addNewListButton" text="Add" var="addListButtonText" />
<spring:theme code="shoppinglist.metaAction.editList" text="Add" var="editList" />
<spring:theme code="shoppinglist.metaAction.editList.SaveButton" text="Add" var="saveEditedList" />
<spring:theme code="shoppinglist.metaAction.deleteList" text="Add" var="deleteList" />
<spring:theme code="shoppinglist.metaAction.delete.lightbox.title" text="Delete List?" var="lightboxTitle" />
<spring:theme code="shoppinglist.metaAction.delete.lightbox.message" var="lightboxMessage" />
<spring:theme code="shoppinglist.metaAction.delete.lightbox.button" var="lightboxConfirmButtonText" />
<spring:theme code="shoppinglist.metaAction.delete.lightbox.buttonDeny" var="lightboxDenyButtonText" />

<c:set var="textTruncationIndicator" value="..." />
<c:set var="maxListNameLength" value="30" />
<spring:theme code="shoppingList.default" text="Shopping List" var="shoppingListName" />
<ul class="shopping-lists">
	<c:forEach items="${shoppingListsList}" var="list">
		<li class="editable-list-item">
			<c:set var="listName" value="${list.name}"/>
			<c:if test="${list.name=='Shopping List'}">
				<c:set var="listName" value="${shoppingListName}"/>							
			</c:if>
			<a href="/shopping/${list.uniqueId}" id="list-item-${list.uniqueId}" class="shopping-list${list.uniqueId == currentList.uniqueId ? ' active' : '' }">
				<span class="list-icon"><i></i></span>
				<span class="list-name ellipsis" data-save-button-text="${saveEditedList}" data-list-id="${list.uniqueId}">${fn:escapeXml(listName)}</span>
				<span class="list-item-count">${list.totalUnitCount}</span>
			</a>
			<nav class="ctrls">
				<a class="btn btn-list-edit" title="${editList}" href="#"><span class="vh">${editList}</span><i></i></a>
				<c:if test="${(fn:length(shoppingListsList)) > 1}">
					<form:form action="/shopping/delete" class="form-delete-list" method="post" id="form-${list.uniqueId}">
						<input type="hidden" name="listId" value="${list.uniqueId}" />
						<a class="btn btn-list-delete"
						   title="${deleteList}"
						   data-list-id="${list.uniqueId}"
						   data-lightbox-title="${lightboxTitle}"
						   data-lightbox-message="${lightboxMessage}"
						   data-lightbox-btn-deny="${lightboxDenyButtonText}" 
						   data-lightbox-show-confirm-button="true"
						   data-lightbox-btn-conf="${lightboxConfirmButtonText}"
						   href="#"><span class="vh">${deleteList}</span><i></i></a>
					</form:form>
				</c:if>
			</nav>
		</li>
	</c:forEach>
	<li class="add-new-list base">
		<form:form action="/shopping/create" method="post">
			<label class="input-label" for="listName">${addListLabel}</label>
			<button type="submit" class="btn btn-primary add-new-list-button">${addListButtonText}</button>
	  		<div class="input-wrapper" >
				<input class="input-field new-list-name validate-empty" name="listName" type="text" value="" placeholder="${addListLabel}" maxlength="255" />
	   		</div>
		</form:form>
	</li>
</ul>
<div id="popover-required-error" class="hidden">
    <div class="popover top popover-error-required">
        <div class="arrow"></div>
        <p class="popover-title"><spring:message code="validate.error.required" /></p>
    </div>
</div>

<script id="tmpl-shoppinglist-new-list-item" type="text/x-template-dotjs">
	<li class="editable-list-item">
			<a href="/shopping/{{=it.listId}}" id="list-item-{{=it.listId}}" class="shopping-list">
				<span class="list-icon"><i></i></span>
				<span class="list-name ellipsis" data-save-button-text="${saveEditedList}" data-list-id="{{=it.listId}}">{{=it.listName}}</span>
				<span class="list-item-count">{{=it.listCount}}</span>
			</a>
			<nav class="ctrls">
				<a class="btn btn-list-edit" title="${editList}" href="#"><span class="vh">${editList}</span><i></i></a>
				<form:form action="/shopping/delete" class="form-delete-list" method="post" id="form-{{=it.listId}}">
					<input type="hidden" name="listId" value="{{=it.listId}}" />
					<a class="btn btn-list-delete" title="${deleteList}"
					   data-list-id="{{=it.listId}}"
					   data-lightbox-title="${lightboxTitle}"
					   data-lightbox-message="${lightboxMessage}"
					   data-lightbox-btn-deny="${lightboxDenyButtonText}"
					   data-lightbox-btn-conf="${lightboxConfirmButtonText}"
					   href="#"><span class="vh">${deleteList}</span><i></i></a>
				</form:form>
			</nav>
		</li>
</script>

<script id="tmpl-shoppinglist-validation-error-empty" type="text/template">
	<spring:message code="validate.error.required" />
</script>
