<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<select id="select-shoppinglist-bulk-action" name="shoppinglist-bulk-action" class="selectpicker borderhelper" data-pretext='<spring:message code="shoppinglist.bulkAction.dropdownPretext" />: '>
	<option class="option option-default" value="bulkActionDefault"><spring:message code="shoppinglist.bulkAction.default" /></option>
	<option class="option option-cart" value="bulkActionCart"><spring:message code="shoppinglist.bulkAction.addToCart" /><i></i></option>
	<option class="option option-favorite" value="bulkActionFavorite"><spring:message code="shoppinglist.bulkAction.addToFavorite" /><i></i></option>
	<option class="option option-compare" value="bulkActionCompare"><spring:message code="shoppinglist.bulkAction.compare" /><i></i></option>
	<option class="option option-remove" value="bulkActionRemove"><spring:message code="shoppinglist.bulkAction.removeFromList" /><i></i></option>
</select>
