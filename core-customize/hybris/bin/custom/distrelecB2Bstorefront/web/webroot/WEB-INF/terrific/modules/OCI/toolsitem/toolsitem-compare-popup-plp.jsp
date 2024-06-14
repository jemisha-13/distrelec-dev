<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="plp-compare">

	<div class="plp-compare__header">
		<span class="plp-compare__item-added"><spring:message code="text.item.added" /></span>
	</div>

	<ul class="plp-compare__content">
		<li> <span class="plp-compare__content__selected-text">( <span class="plp-compare__compare-count">${compareListSize}</span>&nbsp;<spring:message code="text.selected" /> ) </span></li>
		<li>
			<a href="/compare" class="plp-compare__compare-link">
				<i class="fas fa-exchange-alt"></i><br>
				<spring:message code="toolsitem.compare" />
			</a>
		</li>
		<li>
			<a href="#plp-filter-product-list">
				<i class="fas fa-arrow-up"></i><br>
				<spring:message code="text.selectiontotop" />
			</a>
		</li>
		<mod:toolsitem template="toolsitem-compare-remove-all-plp" skin="compare-remove-all-plp" tag="li" />

	</ul>

</div>