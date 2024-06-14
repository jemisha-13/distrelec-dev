<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:message code='import-tool.file.limitexhausted' arguments="" var="fileUplloadLimitMsg"/>
<spring:message code='import-tool.file.saved' arguments="" var="fileUplloadSavedMsg"/>
<spring:message code='import-tool.file.updated' arguments="" var="fileUplloadUpdateMsg"/>

<c:set var="isEShopGroup" value="false"/>
<sec:authorize access="hasAnyRole('ROLE_B2BEESHOPGROUP','ROLE_OCICUSTOMERGROUP','ROLE_ARIBACUSTOMERGROUP','ROLE_CXMLCUSTOMERGROUP')">
	<c:set var="isEShopGroup" value="true"/>
</sec:authorize>

<c:set var="mpnDuplicateProductCount" value="${fn:length(duplicateMpnProductList)}" />

<c:forEach items="${unavailableProducts}" var="unavailableProduct">

	<c:set var="productReferences" value="${unavailableProduct.product.productReferences}"/>

	<c:choose>
		<c:when test="${fn:length(productReferences) gt 0}">
			<c:set var="unavailableProductsWithAlternative" value="${unavailableProductsWithAlternative + 1}" />
		</c:when>
		<c:otherwise>
			<c:set var="unavailableProductsNoAlternative" value="${unavailableProductsNoAlternative + 1}" />
		</c:otherwise>
	</c:choose>

</c:forEach>

<c:set var="alternativeCount" value="${unavailableProductsWithAlternative + mpnDuplicateProductCount}" />

<mod:global-messages template="component" skin="component bom hidden"  headline='' body='' type="error"/>

<div class="bd cf row mod-bom-toolbar__row">
    <div class="_left col">
		<c:if test="${not isEShopGroup}">
			<button class="btn-add-shopping mat-button mat-button--action-grey" data-aainteraction="add all to shopping list">
				<span class="addto"> <spring:message code='text.addselectedtoshoppinglist' /> </span>
				<span class="added"> <spring:message code='text.shoppinglistadded' /> </span>
			</button>
		</c:if>

		<c:choose>
			<c:when test="${loadFile}">
				<button class="btn-bom-updatefile mat-button mat-button--action-grey disabled" disabled>
					<spring:message code="update.bomfile" />
				</button>
			</c:when>
			<c:otherwise>
				<button class="btn-bom-savefile mat-button mat-button--action-grey" data-aainteraction="save BOM file">
					<spring:message code="text.savebomfile" />
				</button>
			</c:otherwise>
		</c:choose>

		<button class="btn-bom-updatefile hidden mat-button mat-button--action-grey">
			<spring:message code="update.bomfile" />
		</button>

	</div>
    <div class="_right col">

		<button class="btn-bom hidden">
			<spring:message code="price.match.getquote" />
		</button>

		<button class="btn-add-cart mat-button mat-button__solid--action-green ellipsis"
				data-aainteraction="add to cart"
				data-position="${statusIndex}"
				data-product-code="${product.codeErpRelevant}" title="<spring:message code="toolsitem.add.cart" text="Add to Cart" />">

				<span class="addto"> <spring:message code="toolsitem.add.cart" /> </span>
				<span class="added"> <spring:message code="text.added.to.cart" /> </span>

		</button>

		<button class="btn-view-cart mat-button mat-button__solid--action-blue ellipsis hidden"
				data-aainteraction="view cart"
				data-position="${statusIndex}"
				data-product-code="${product.codeErpRelevant}" title="<spring:message code="metahd.cart.viewCart" text="view cart" />">

				<spring:message code="metahd.cart.viewCart" />

		</button>

    </div>

	<mod:global-messages template="component" skin="component fileuplload-save hidden col-12 success"  headline='' body='${fileUplloadSavedMsg}' type="success"/>
	<mod:global-messages template="component" skin="component fileuplload-update hidden col-12 success"  headline='' body='${fileUplloadUpdateMsg}' type="success"/>
	<mod:global-messages template="component" skin="component fileuplloadlimit-error hidden col-12 error"  headline='' body='${fileUplloadLimitMsg}' type="error"/>

</div>

<!-- Modal -->
<div class="modal" id="noAlternativeSelectedModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-body">
				<p class="modal__title"> <spring:theme code="noalternativeselected.title" /> </p>
				<p class="modal__description" data-alternativeCount="${alternativeCount}">
					<spring:theme code="noalternativeselected.description" arguments="${alternativeCount}" />
				</p>
			</div>
			<div class="modal-footer">

				<div class="row">

					<div class="col-7">
						<button type="button" class="mat-button mat-button--action-grey col btn-continue" > <spring:theme code="noalternativeselected.continuetext" /></button>
					</div>

					<div class="col">
						<button type="button" class="mat-button mat-button--matterhorn mat-button__solid--action-green col" data-dismiss="modal"> <spring:theme code="noalternativeselected.choosetext" /> </button>
					</div>

				</div>

			</div>
		</div>
	</div>
</div>
