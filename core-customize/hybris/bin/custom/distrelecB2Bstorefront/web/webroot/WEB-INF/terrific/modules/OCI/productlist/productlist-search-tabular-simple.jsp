<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<c:set var="productFamily" value="false" />
<c:choose>
    <c:when test="${not empty param['filter_productFamilyCode']}">
        <c:set var="productFamily" value="true" />
    </c:when>
    <c:when test="${not empty param['filter_CuratedProducts']}">
        <c:set var="productFamily" value="true" />
    </c:when>
    <c:otherwise>
        <c:set var="productFamily" value="false" />
    </c:otherwise>
</c:choose>
<c:set var="requestPath" value="${requestScope['javax.servlet.forward.request_uri']}"/>
<c:if test = "${productFamily =='false' && fn:contains(requestPath, '/new')}">
	<c:set var="productFamily" value="true" />
</c:if>
<c:if test = "${productFamily =='false' && fn:contains(requestPath, '/clearance')}">
	<c:set var="productFamily" value="true" />
</c:if>
<c:if test = "${productFamily =='false' && fn:contains(requestPath, '/manufacturer/')}">
	<c:set var="productFamily" value="true" />
</c:if>

<c:set var="EditCart" value="${(empty EditCart or EditCart) and not isSubItem}" />

<c:forEach items="${searchPageData.pagination.productsPerPageOptions}" var="productsPerPageOption">
	<c:if test="${productsPerPageOption.selected}">
		<c:set var="pageSize" value="${productsPerPageOption.value}" />
	</c:if>	
</c:forEach>

<c:set var="codeErpRelevant" value="${product.codeErpRelevant == undefined ? 'x' : product.codeErpRelevant}" />

<%-- Variables are needed to determine if the product link should be displayed or not --%>
<c:if test="${product.buyable or eolWithReplacement}">
	<c:choose>
		<c:when	test="${product.buyable and fn:length(product.activePromotionLabels) gt 0}">
			<c:set var="promotionLabelsPresences" value="true" />
			<c:forEach items="${product.activePromotionLabels}"	var="activePromoLabel" end="0">
				<c:set var="promoLabel" value="${activePromoLabel}" />
				<c:set var="label" value="${activePromoLabel.code}" />
				<c:choose>
					<c:when test="${label eq 'noMover' }">
						<c:set var="teasertrackingid" value="${wtTeaserTrackingId}.${codeErpRelevant}-cre.-" />
					</c:when>
					<c:when test="${label eq 'new' }">
						<c:set var="teasertrackingid" value="${wtTeaserTrackingId}.${codeErpRelevant}-new.-" />
					</c:when>
					<c:otherwise>
						<c:set var="teasertrackingid" value="${wtTeaserTrackingId}.${codeErpRelevant}.-" />
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<c:set var="teasertrackingid" value="${wtTeaserTrackingId}.${codeErpRelevant}.-" />
		</c:otherwise>
	</c:choose>
</c:if>

<div class="search-results productlist" data-current-query-url="${searchPageData.currentQuery.url}" data-page="${searchPageData.pagination.currentPage}" data-page-size="${pageSize}">

	<div class="wrapperScrollTop" style="visibility: hidden; margin-top:-20px;">
		<div class="divScrollTop" >
		</div>
	</div>

	<div class="wrapperScrollTable wrapperScrollTableShort">	
	
		<div class="divScrollTable divScrollTable-s">	
			
			<table class="tabular-search-table list">

				<!-- START OF HEADER -->
			
		        <tr class="tr_header">
		        	<td class="headcol headcol-s">
						<div class="product-image-header">
							<div class="label">   </div>						
						</div>		 		        	
						<div class="product-detail-header">
							<div class="label"><spring:message code="product.product.details" />  </div>						
						</div>
						<div class="price-header">
							<div class="label"><spring:message code="quotationhistory.price" /> (${not empty searchPageData.currencyIso ? searchPageData.currencyIso : currentCurrency.isocode})</div>  
							<div class="sorting-arrows" id="Price">
								<div class="arrow-up" title="<spring:message code="tabularview.ascending" /> "></div>
								<div class="arrow-down" title="<spring:message code="tabularview.descending" /> "></div>
							</div>											
						</div>
						<div class="availability-header">
							<div class="label"><spring:message code="product.available" /> </div>
							<div class="sorting-arrows" id="totalInStock">
								<div class="arrow-up" title="<spring:message code="tabularview.ascending" /> "></div>
								<div class="arrow-down" title="<spring:message code="tabularview.descending" /> "></div>
							</div>						
						</div>						
					</td>								
					
		        	<td class="long">
		        		<div class="header-attribute-label">
		        			<div class="label"><spring:message code="product.manufacturer" /> </div>
							<div class="sorting-arrows" id="Manufacturer">
								<div class="arrow-up" title="<spring:message code="tabularview.ascending" /> "></div>
								<div class="arrow-down" title="<spring:message code="tabularview.descending" /> "></div>
							</div>		        			
		        		</div>
		        	</td>

		        </tr>		

				<!-- END OF HEADER -->


				<!-- START OF TABLE BODY -->
		        
		        <c:forEach items="${searchPageData.results}" var="product" varStatus="status">
					
		  
			        <tr class="tr_content productCode-${product.codeErpRelevant}">
			        	
			        	<!-- HEADER -->

			        	
			        	<td class="headcol headcol-s">
			        		<c:set var="productsPerPage" value="10" />
							<c:forEach items="${searchPageData.pagination.productsPerPageOptions}" var="productsPerPageOption">
				        		<c:if test="${productsPerPageOption.selected}">
									<c:set var="productsPerPage" value="${productsPerPageOption.value}" />
								</c:if>
							</c:forEach>
							<c:set var="currentPage" value="${searchPageData.pagination.currentPage-1}" />
							<c:set var="productPostion" value="${((searchPageData.pagination.currentPage-1)* productsPerPage)+status.index+1}" />
						  	<c:url value="${product.url}" var="productTrackUrl">
						   	 	 <c:param name="pos" value="${productPostion}"/>
						   	 	 <c:param name="origPos" value="${product.origPosition}"/>
						      	 <c:param name="origPageSize" value="${productsPerPage}"/>
						      	 <c:param name="q" value="${param['q']}"/>
						      	 <c:param name="p" value="${product.categoryCodePath}"/>
						      	 <c:param name="prodprice" value="${product.price.value}"/>
						      	 <c:param name="isProductFamily" value="${productFamily}"/>
						      	 <c:param name="campaign" value="${product.campaign}"/>
							</c:url>
			        		<div class="image-cell-l">
								<div class="productlabel-wrap">
									<c:forEach items="${product.activePromotionLabels}" var="promoLabel" end="0">
										<mod:product-label promoLabel="${promoLabel}" />
									</c:forEach>
								</div>
								<c:if test="${not empty product.energyEfficiency || not empty product.energyClassesFitting || not empty product.energyClassesBuiltInLed}">
						        	<mod:energy-efficiency-label skin="tabular" product="${product}" />
								</c:if>
								<c:set var="productImage" value="${product.productImages[0]}" />
								<c:set var="landscapeSmall" value="${not empty productImage.landscape_small_webp.url ? productImage.landscape_small_webp.url : not empty productImage.landscape_small.url ? productImage.landscape_small.url : '/_ui/all/media/img/missing_landscape_small.png'}" />
								<div class="image-container">
									<a href="${productTrackUrl}" class="teaser-link" data-position="${position}">
											<img alt="<spring:message code='product.page.title.buy' text='Buy' arguments='${product.name}' argumentSeparator='!!!!' />"
												src="${landscapeSmall}" />
									</a>
								</div>
								<div class="ctrls">
									<mod:product-tools template="tabular" productId="${product.code}" />
								</div>
							</div>
							<div class="image-and-description">
								<div class="title-container" title="${product.name}">
									<a href="${productTrackUrl}" class="teaser-link" data-position="${position}">
										<span class="ellipsis">
												${product.name}
										</span>
									</a>
								</div>
										
								<div class="table-row container-sub-info-article">
									<div class="table-cell first">
										<span class="label"><spring:message code="product.articleNumber" />:</span> 
										<span class="value"><format:articleNumber articleNumber="${product.codeErpRelevant}"  /></span>
									</div>
									<div class="table-cell second ellipsis">
										<span class="label"><spring:message code="product.typeName" />:</span>
										<span title="${product.typeName}" class="value type-name">${product.typeName}</span>
									</div>
								</div>
								<c:if test="${(currentChannel.type eq 'B2B' and product.availableToB2B) or (currentChannel.type eq 'B2C' and product.availableToB2C)}">
									<mod:numeric-stepper template="productlist-search-tabular" skin="productlist-search-tabular" product="${product}" />
								</c:if>

								<%-- Used by product and productlist module --%>
									<c:set var="productsPerPage" value="10" />
									<c:set var="postion" value="${((searchPageData.pagination.currentPage-1)* productsPerPage)+status.index+1}" />
						        	<c:forEach items="${searchPageData.pagination.productsPerPageOptions}" var="productsPerPageOption">
						        		<c:if test="${productsPerPageOption.selected}">
											<c:set var="productsPerPage" value="${productsPerPageOption.value}" />
										</c:if>
									</c:forEach>
									<input type="hidden" class="hidden-product-code" value="${product.code}" />
									<input type="hidden" class="hidden-price" value="${product.price.value}" />
									<input type="hidden" class="hidden-origPosition" value="${product.origPosition}" />
									<input type="hidden" class="hidden-categoryCodePath" value="${product.categoryCodePath}" />
									<input type="hidden" class="hidden-searchQuery" value="${param['q']}" />
									<input type="hidden" class="hidden-origPageSize" value="${productsPerPage}" />
									<input type="hidden" class="hidden-pos" value="${postion}" />
									<input type="hidden" class="hidden-productFamily" value="${productFamily}" />
									<input type="hidden" class="hidden-productCampaign" value="${product.campaign}" />
								<%-- End product and productlist module --%>
								
								<c:choose>
									<c:when test="${(currentChannel.type eq 'B2B' and product.availableToB2B) or (currentChannel.type eq 'B2C' and product.availableToB2C)}">
										<div class="button-holder button-holder--true">
											<button class="btn btn-primary btn-cart fb-add-to-cart" data-product-code="${product.codeErpRelevant}" title="Add to Cart">
												<i></i>
											</button>
										</div>
									</c:when>
									<c:otherwise>
										<spring:message code="article.business.only.error" />
									</c:otherwise>
								</c:choose>
							</div>

							<div class="price-and-stepper prices-cart-column" >
								<mod:scaled-prices product="${product}" template="prices-tabular" skin="single-tabular" />
							</div>			        	

							<div class="availability ImInTabularViewSimple">
								<mod:shipping-information template="compare-list" product="${product}" skin="comparelist" />								
							</div>			        	
						</td>
						
						<!-- Manufacturer-->
			        	<td class="long manufacturer">
							<div class="value tabularAttributeValue">${product.distManufacturer.name}</div>	
			        	</td>	
			        </tr>	        
		        </c:forEach>
				<!-- END OF TABLE BODY -->
			</table>	
		</div>			
	</div>			
</div>

<spring:message code="product.list.facetAjax.loadProducts" var="loadingStateMessage" />
<mod:loading-state skin="loading-state" loadingStateMessage="${loadingStateMessage}" />

<div class="ajax-product-loader js-apply-facets">
	<div class="background-overlay apply-facets"></div>
	<div class="message-wrapper js-apply-facets">
		<div class="loading-message apply-facets reload-message" >
			<a href="" class="js-update-result" data-current-query-url="${searchPageData.currentQuery.url}" >
				<span>
					<spring:message code="product.list.reloadLayer" />
				</span>
			</a>
		</div>
	</div>
</div>



