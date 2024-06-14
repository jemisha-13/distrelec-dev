<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="maxVisibleOtherAttr" value="100" />
<c:set var="positionTr" value="1" />

<div class="tableGrid">
	<div class="tableGrid__mobile">
		<mod:compare-list-attributes product="${product}"/>
	</div>
	<ul class="tableGrid__items">
		<c:forEach items="${product.possibleOtherAttributes}" var="possibleAttrKey" varStatus="attrStatus">
			<c:set var="otherAttr" value="${product.otherAttributes[possibleAttrKey.key]}" />
			<li class="tableGrid__items__item tableGrid__items__item__attr otherAttributes_product__${attrStatus.index}">
				<c:choose>
					<c:when test="${not empty otherAttr}">
						<c:set var="featuresCount" value="${fn:length(otherAttr.featureValues)}"/>
						<c:choose>
							<c:when test="${featuresCount eq 1}">
								<div class="item-single">
									<c:forEach items="${otherAttr.featureValues}" var="value" varStatus="status" >
										<span class="feature-value constrain">
											${value.value}
											<c:choose>
												<c:when test="${otherAttr.range}">
													${not status.last ? '-' : otherAttr.featureUnit.symbol}
												</c:when>
												<c:otherwise>
													${otherAttr.featureUnit.symbol}
													${not status.last ? ' ,  ' : ''}
												</c:otherwise>
											</c:choose>
										</span>
									</c:forEach>
								</div>
							</c:when>
							<c:otherwise>
								<div class="item-multi">
									<c:forEach items="${otherAttr.featureValues}" var="value" varStatus="status" >
										<span class="feature-value constrain constrain--single">
											${value.value}
											<c:choose>
												<c:when test="${otherAttr.range}">
													${not status.last ? '-' : otherAttr.featureUnit.symbol}
												</c:when>
												<c:otherwise>
													${otherAttr.featureUnit.symbol}
													${not status.last ? ',  ' : ''}
												</c:otherwise>
											</c:choose>
										</span>
									</c:forEach>
								</div>
							</c:otherwise>
						</c:choose>
					</c:when>
				</c:choose>
			</li>
		</c:forEach>

		<c:forEach items="${product.commonAttributes}" var="mapEntry" varStatus="attrStatus">
			<c:set var="feature" value="${mapEntry.value}" />
				<c:choose>
					<c:when test="${feature.featureValues != null}">
						<li class="tableGrid__items__item tableGrid__items__item__attr commonAttributes_product__${attrStatus.index}">
							<c:forEach items="${feature.featureValues}" var="value" varStatus="status">
									<span class="feature-value constrain">
										${value.value}&nbsp;
										<c:choose>
											<c:when test="${feature.range}">
												${not status.last ? '-' : feature.featureUnit.symbol}
											</c:when>
											<c:otherwise>
												${feature.featureUnit.symbol}
												${not status.last ? '<br/>' : ''}
											</c:otherwise>
										</c:choose>
									</span>
							</c:forEach>
						</li>
					</c:when>
					<c:otherwise>
						<li class="tableGrid__items__item tableGrid__items__item__attr commonAttributes_product__${attrStatus.index}">
							<span class="feature-value constrain">-</span>
						</li>
					</c:otherwise>
				</c:choose>
			<c:set var="positionTr" value="${positionTr+1}" />
		</c:forEach>

	</ul>
</div>

<c:if test="${fn:length(product.otherAttributes) > maxVisibleOtherAttr}">
	<div class="row show-more"><a class="show-more-link btn-show-more" href="#"><span><spring:message code="product.accordion.show.more" /></span></a></div>
</c:if>
