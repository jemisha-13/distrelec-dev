<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:message code="lightboxshoppinglist.placeholder" var="sPlaceholder" />
<spring:message code="lightboxshoppinglist.new.shopping.list" text="New shopping list" var="sNewLabel"/>
<spring:message code="lightboxshoppinglist.check.one" text="You should at least select one shopping list" var="sOneList"/>

<div class="modal base" id="modalShoppingList" tabindex="-1">
    <div class="hd">
        <div class="-left"> <h3 class="title"><spring:message code="lightboxshoppinglist.save.product.following.list" /></h3> </div>
        <div class="-right"> <a class="btn btn-close" href="#" data-dismiss="modal" aria-hidden="true"><spring:message code="lightboxshoppinglist.close" /></a> </div>
    </div>
    <div class="bd">
        <div class="error-box">
            <h4><spring:message code="lightboxshoppinglist.error" />!</h4>
            <p><spring:message code="lightboxshoppinglist.message.error" /></p>
        </div>
        <div class="receive-box">
            <h4><spring:message code="lightboxshoppinglist.error" />!</h4>
            <p><spring:message code="lightboxshoppinglist.message.error.receive" /></p>
        </div>
        <div class="start-box">
            <div class="form-row">
                <ul class="box checkbox-group target-items validate-checkbox-group" data-custom-error-message="${sOneList}"></ul>
            </div>
            <div class="form-row row-new-shopping-list">
            	<label class="newShoppingList">${sNewLabel}</label>
            	<input name="newShoppingListName" class="new-shopping-list-input field ipt-big validate-empty" placeholder="${sPlaceholder}" maxlength="40" type="text" value="">
            </div>
        </div>
    </div>
    <div class="ft">
        <button type="submit" class="btn btn-secondary btn-cancel" data-dismiss="modal" aria-hidden="true"><spring:message code="lightboxshoppinglist.cancel" text="Cancel" /></button>
        <button type="submit" class="btn btn-primary btn-send" ><spring:message code="lightboxshoppinglist.okay" text="Ok" /></button>
    </div>
</div>
<script id="tmpl-lightbox-shoppinglist-list-item" type="text/x-template-dotjs">
    {{~it :item:id }}
    <li>
        <input id="{{=item.id}}" type="checkbox" name="{{=item.id}}" />
        <label class="ellipsis" for="{{=item.id}}"><i></i>{{=item.value}} <span>{{=item.count}}</span></label>
    </li>
    {{~}}
</script>
<script id="tmpl-lightbox-shoppinglist-validation-error-checkboxgroup" type="text/template">
	<spring:message code="validate.error.checkboxgroup" />
</script>