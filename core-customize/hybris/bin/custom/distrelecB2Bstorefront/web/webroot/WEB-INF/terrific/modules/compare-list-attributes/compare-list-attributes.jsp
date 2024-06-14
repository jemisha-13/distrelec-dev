<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="compare.item.attributes.other" var="sCompareAttributes"/>
<spring:message code="product.accordion.show.more" var="sProductShowMore"/>

<c:set var="maxVisibleOtherAttr" value="100" />
<c:set var="positionTr" value="1" />

<ul class="attribute-title">
	<li>${sCompareAttributes}</li>
</ul>

<div class="tableGrid__feature">
	<ul class="tableGrid__feature__items possibleOtherAttributes">
		<c:forEach items="${product.possibleOtherAttributes}" var="attribute" varStatus="loop">
			<li class="tableGrid__feature__items__item possibleOtherAttributes__${loop.index}">
				<span>${attribute.value}</span>
			</li>
			<c:set var="positionTr" value="${positionTr+1}" />
		</c:forEach>
	</ul>

	<ul class="possibleCommonAttributes">
		<c:forEach items="${product.possibleCommonAttributes}" var="attribute" varStatus="loop">
			<li class="tableGrid__feature__items__item possibleCommonAttributes__${loop.index}">
				<span>${attribute.value}</span>
			</li>
			<c:set var="positionTr" value="${positionTr+1}" />
		</c:forEach>
	</ul>

</div>


<c:if test="${fn:length(product.otherAttributes) > maxVisibleOtherAttr}">
	<div class="row show-more"><a class="show-more-link btn-show-more" href="#"><span>${sProductShowMore}</span></a></div>
</c:if>
