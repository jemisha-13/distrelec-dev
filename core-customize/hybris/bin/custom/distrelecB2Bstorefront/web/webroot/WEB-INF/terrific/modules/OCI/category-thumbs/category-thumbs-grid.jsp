<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<ul class="categories md-system">
	<c:forEach items="${gridItems}" var="gridItem">
		
		<c:set var="categoryCode" value="${gridItem.title}" />  
		
		<c:if test="${!ycommerce:isCategoryPunchedout(categoryCode, request)}">
			<li class="col-sm-6 col-md-3 col-lg-2 categories__category">
		
				<c:set var="categoryName" value="${fn:replace(gridItem.title, ' ', '-')}" /> 
	
				<a class="thumb-link categories__link" href="${gridItem.url}" name="${wtTeaserTrackingId}.${fn:toLowerCase(categoryName)}.-">
					<div class="categories__image">
						<img alt="<c:out value="${gridItem.title}" />" src="${gridItem.thumbnail.url eq null ?  '/_ui/all/media/img/missing_portrait_small.png' : gridItem.thumbnail.url}" />
					</div>
					<div class="categories__name" >
						<span class="categories__title" title="<c:out value="${gridItem.title}" />">${gridItem.title}</span>
						<span class="categories__arrow"><i class="angle-right"></i></span>
					</div>
				</a>
			</li>
		</c:if>		
	</c:forEach>
</ul>
