<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:message code="cart.emptied.message" text="You have emptied your cart." var="sEmptiedMessage" />
<spring:message code="cart.emptied.undo" text="Undo" var="sEmptiedUndo" />

<div class="mod-cart-emptied__holder">
    <div class="mod-cart-emptied__holder__image">
        <i class="fas fa-exclamation"></i>
    </div>
    <div class="mod-cart-emptied__holder__content">
        ${sEmptiedMessage}<span class="undo">${sEmptiedUndo}</span>
    </div>
</div>
