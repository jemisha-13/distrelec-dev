<div class="productlist-price-recalc-wrapper js-productlist-price-recalc-wrapper" id="wrap-cart-recalculate-layer-shopping">
    <div class="productlist-price-recalc-wrapper__inner">
        <c:if test="${fn:length(currentList.entries) != 0 }">
            <mod:cart-pricecalcbox
                    skin="shopping"
                    template="shopping"
                    cartData="${cartData}"
                    showTitle="true"
                    currentList="${currentList}"
            />
        </c:if>
        <mod:cart-recalculatelayer template="shopping" skin="shopping" />
    </div>
</div>