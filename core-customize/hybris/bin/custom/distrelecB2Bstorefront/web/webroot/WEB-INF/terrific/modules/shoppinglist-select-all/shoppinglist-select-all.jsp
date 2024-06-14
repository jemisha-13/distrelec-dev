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
<spring:theme code="shoppinglist.metaAction.delete.lightbox.message" text="" var="lightboxMessage" />
<spring:theme code="shoppinglist.metaAction.delete.lightbox.button" text="" var="lightboxConfirmButtonText" />
<spring:theme code="shoppinglist.products.selectall" text="Select All" var="sSelectAllLink" />


<form:form class="base" action="#" method="GET">
	<input id="select-all" class="checkbox select-all" type="checkbox" value="select-all" name="checkboxes" ${disabledState ? 'disabled="disabled"' : ''}>
	<label class="select-all-label ellipsis" title="${sSelectAllLink}" for="select-all" ${disabledState ? 'disabled="disabled"' : ''}>${sSelectAllLink}</label>
</form:form>



