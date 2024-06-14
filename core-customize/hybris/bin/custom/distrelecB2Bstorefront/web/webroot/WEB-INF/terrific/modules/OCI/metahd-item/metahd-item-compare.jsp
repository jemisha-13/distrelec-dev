<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:url var="goToCompareUrl" value="/compare"/>
<div class="menuitem-wrapper">
	<a class="menuitem popover-origin" href="${goToCompareUrl}"> <span class="vh"><spring:message code="metahd.compare" /></span> <i></i> </a>
	<section class="flyout">
		<div class="hd">
			<div class="-left"> <h3><spring:message code="metahd.compare" /></h3><small><spring:message code="metahd.compare.products" /></small> </div>
			<div class="-right"> <a aria-hidden="true" data-dismiss="modal" href="#" class="btn btn-close"><spring:message code="base.close" /></a> </div>
		</div>
		<div class="bd"> <%-- doT target --%> </div>
		<div class="ft"> <a class="show-all" href="${goToCompareUrl}"><spring:message code="metahd.compare.showall" /></a> </div>
	</section>
</div>
<%-- flyout doT template --%>
<script id="template-metahd-compare-row" type="text/x-template-dotjs">
	<article class="compare-row" data-uri="{{=it.productUrl}}">
		<div class="cell cell-img">
			{{?it.thumbUrl}}
			<picture>
				<source sourceset="{{=it.thumbUrlWebp}}">
				<img src="{{=it.thumbUrl}}" alt="" />
			</picture>
			{{?}}
		</div>
		<div class="cell cell-product">
			<span class="constrain">
				<span class="product-name">{{=it.name}}</span>
				<span class="product-price">{{=it.priceLocal}}</span>
			</span>
		</div>
	</article>
</script>