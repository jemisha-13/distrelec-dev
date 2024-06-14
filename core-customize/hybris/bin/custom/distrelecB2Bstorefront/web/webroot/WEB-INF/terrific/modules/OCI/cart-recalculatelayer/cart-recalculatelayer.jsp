<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:message code="cart.recalculatelayer.recalc" text="Recalculate" var="recalculate"/>

<div class="bd-cart-recalculate-layer">
    <div class="back-cart-recalculate-layer"></div>
    <div class="btn-recalculate-wrap">
        <a class="btn-secondary btn-recalculate"><span>${recalculate}</span><img src="/_ui/all/media/img/throbber_small_grey_white_matte.gif" /></a>
    </div>
</div>
