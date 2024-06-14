<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<spring:message code="text.quote" text="Quote" var="sQuote"/>

<div class="skin-cart-list-item-cart__holder">
	<div class="skin-cart-list-item-cart__holder__image">
		<div class="image-wrap">
			<img alt="${imageAlt}" src="/_ui/all/media/img/missing_portrait_small.png" />
		</div>
	</div>
	<div class="skin-cart-list-item-cart__holder__content skin-cart-list-item-cart__holder__content--quote">
		<h3 class="ellipsis quote-title" title="${sQuote}">${sQuote}</h3>
		<div class="cell-info-table">
			<div class="cell-info-cell">
				<div class="hd"><spring:message code="cart.list.quoteNumber" text="Quote Nr." /></div>
				<div class="bd ellipsis" title="${orderEntry.quotationId}">${orderEntry.quotationId}</div>
			</div>
			<div class="cell-info-cell">
				<div class="hd"><spring:message code="cart.list.quoteReference" text="Quote Reference"/></div>
				<div class="bd ellipsis" title="<c:out value="${orderEntry.quotationReference}" />">${orderEntry.quotationReference}</div>
			</div>
		</div>
		<div class="quote-includes">
			<spring:message code="text.quote.includes" text="Quote includes:" />
		</div>
	</div>

</div>
