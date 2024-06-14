<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<spring:message code="article-number.brand" var="sBrandTitle" />
<spring:message code="product.articleNumberNew" var="sArticleNumberText" />
<spring:message code="product.articleNumber.copy" var="sArticleCopy" />
<spring:message code="product.typeNameNew" var="sTypeNumberText" />
<spring:message code="product.alias.text" var="sAliasText" />
<spring:message code="product.family.linkTextExtended" var="sProductFamilyLinkText"/>


<c:set var="productFamilyUrl" value="${product.productFamilyUrl}" />
<c:set var="reevooProductId" value="${request.getParameter('reevoo-product-id')}"/>

<c:choose>
	<c:when test="${product.catPlusItem }">
		<div class="elem">
			<span><spring:message code="product.catalogPlus.articleNumber" /></span>&nbsp;${product.catPlusSupplierAID}
		</div>
	</c:when>
	<c:otherwise>
		<div class="elem elem--first js-product-code" data-product-code="<format:articleNumber articleNumber="${product.codeErpRelevant}" />">
			<span class="title ellipsis" id="article-number-text">${sArticleNumberText}:</span>
			<span id="myText" class="a12"><format:articleNumber articleNumber="${product.codeErpRelevant}" /></span>
			<div class="elem__holder">
				<span class="copy-button-1" data-aainteraction="copy text" data-text-title="Distrelec Article Number" data-text-value="${product.codeErpRelevant}">
				<i class="far fa-copy" aria-hidden="true"></i>
			</span>
				<div class="article-tooltip" id="article-tooltip">
					<div class="article-tooltip__content">
						<p>
							<format:articleNumber articleNumber="${product.codeErpRelevant}" />&nbsp;${sArticleCopy}
						</p>
					</div>
				</div>
			</div>
		</div>
	</c:otherwise>
</c:choose>

<c:if test="${ not empty product.typeName }">
	<div class="elem elem--mpn" title="${product.typeName}">
        <span class="title">${sTypeNumberText}:</span>
		<span id="copyTypeName" class="a12 ellipsis">${product.typeName}</span>
		<div class="elem__holder">
			<span class="copy-button" data-aainteraction="copy text" data-text-title="Manufacturer Part Number" data-text-value="${product.typeName}">
				<i class="far fa-copy" aria-hidden="true"></i>
			</span>
			<div class="article-tooltip" id="article-tooltip-1">
				<div class="article-tooltip__content">
					<p>
						${product.typeName}&nbsp;${sArticleCopy}
					</p>
				</div>
			</div>
		</div>
    </div>
</c:if>

<c:if test="${ not empty product.alternativeAliasMPN}">
	<div class="elem elem--alias">
		<span class="title">${sAliasText}: </span>
		<c:forTokens var="sAltAliasMPN" items="${product.alternativeAliasMPN}" delims="|">

			<div class="elem__alias-item" title="${sAltAliasMPN}">
				<span class="a12 ellipsis copyTypeNameAlias">${sAltAliasMPN}</span>
				<div class="elem__holder">
					<span class="copy-button-alias" data-aainteraction="copy text" data-text-title="Manufacturer Part Number" data-text-value="${sAltAliasMPN}&nbsp;${sArticleCopy}">
						<i class="far fa-copy" aria-hidden="true"></i>
					</span>
					<div class="article-tooltip">
						<div class="article-tooltip__content">
							<p>
									${sAltAliasMPN}&nbsp;${sArticleCopy}
							</p>
						</div>
					</div>
				</div>
			</div>

		</c:forTokens>
	</div>
</c:if>

<c:if test="${not empty distManufacturer}">
	<div class="elem elem--brand" title="${product.typeName}">
		<span class="title">${sBrandTitle}:</span>

		<c:if test="${showLink}">
			<c:choose>
				<c:when test="${showExternalLink}">
					<a title="${distManufacturer.name}" class="a12 brand ellipsis" href="${distManufacturer.url}" target="_blank">
				</c:when>
				<c:otherwise>
					<a title="${distManufacturer.name}" class="a12 brand ellipsis" href="${distManufacturer.urlId}">
				</c:otherwise>
			</c:choose>
		</c:if>

			${distManufacturer.name}

		<c:if test="${showLink}">
			</a>
		</c:if>

		<c:if test="${not empty productFamilyUrl}">
			<span>|</span>
			<a class="a12 brand ellipsis"
			   title="${sProductFamilyLinkText}"
			   href="<c:out value="${productFamilyUrl}"/>"
			   data-aainteraction="product family click"
			   data-link-text="${englishMessageSourceAccessor.getMessage('product.family.linkTextExtended')}"
			   data-location="pdp"
			>${sProductFamilyLinkText}</a>
		</c:if>
	</div>
</c:if>

<c:if test="${isReevooActivatedForWebshop && product.eligibleForReevoo}">	
	<reevoo-product-badge
		variant='PDP'
		SKU="${product.code}"
		id="reevoo_badge_pdp"
			on-click="
			window.scroll({ top: document.querySelector('#reevoo_tabbed').offsetTop - 100, behavior: 'smooth' });"
			reevoo-click-action="no_action">
	</reevoo-product-badge>
</c:if>
