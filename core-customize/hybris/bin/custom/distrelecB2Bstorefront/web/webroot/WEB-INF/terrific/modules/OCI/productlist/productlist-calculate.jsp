<div id="wrap-cart-recalculate-layer-shopping">
    <div class="row row-relative">
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