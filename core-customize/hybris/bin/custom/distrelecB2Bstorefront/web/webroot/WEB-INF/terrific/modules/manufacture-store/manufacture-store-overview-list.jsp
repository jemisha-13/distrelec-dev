<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:forEach var='manuCats' items='${manufacturers}' varStatus='statusCat'>

	<div id='alphaNum${manuCats.key}' class='hashJumpPlaceholder'></div>
	<div class='block'>
		<div class="listBlockHeader">
			<h3 class="base manufacturerCategoryTitle">${manuCats.key}</h3>
		</div>
		<div class='listBlockDetail cf'>

			<c:set var="manuCat" scope="page" value="${manuCats.value}"/>
			<c:set var="manuCatSize" scope="page" value="${fn:length(manuCat)}"></c:set>
			<%--List-Block size -> totalManus(x)/cols(4) = listSize(z); Regel: z * 4 >= x; e.g. 9/4 = 2; 2 * 4 (not)>= 9 -> therefore take column size of 3 (1 greater) as z--%>
			<%--Instead of floor(x/4) we use the equivalent (x-(x%1))--%>
			<c:set var="cols" scope="page" value="{1,2,3,4}"></c:set>
			<c:set var="numCols" scope="page" value="${4}"></c:set>
			<c:set var="colSize" scope="page" value="${(manuCatSize/numCols)-((manuCatSize/numCols)%1)}"></c:set>
			<c:choose>
				<c:when test="${colSize*numCols < manuCatSize}">
					<c:set var="colSize" scope="page" value="${colSize+1}"></c:set>
				</c:when>
			</c:choose>
			<%--Loop for each of the four columns--%>
			<c:forEach var='column' items='${cols}' varStatus="col">

				<div class='gu-3'>
					<ul>
						<c:set var="forEachEnd" value="${((col.index+1)*colSize)-1}" />
						<c:if test="${forEachEnd >= 0}">
							<c:forEach var='manufacturer' items='${manuCat}' begin='${col.index*colSize}' end='${forEachEnd}' varStatus='status'>								
								<li>
									<c:set var="targetDetailPage" scope="page" value="/${currentLanguage.isocode}/manufacturer/${manufacturer.nameSeo}/${manufacturer.code}"></c:set>
									<a href='${targetDetailPage}'>
										<span class="ellipsis" title="<c:out value="${manufacturer.name}" />">
											${manufacturer.name}
										</span>
									</a>
								</li>
							</c:forEach>
						</c:if>
					</ul>
				</div>

			</c:forEach>

		</div>
	</div>

</c:forEach>
