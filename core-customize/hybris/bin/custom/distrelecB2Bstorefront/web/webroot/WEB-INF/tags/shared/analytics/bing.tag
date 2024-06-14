<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty microsoftUetTagId}">
    <script>
        (function (w, d, t, r, u) {
            var f, n, i;
            w[u] = w[u] || [], f = function () {
                var o = {
                    ti: ${microsoftUetTagId}
                };
                o.q = w[u], w[u] = new UET(o), w[u].push("pageLoad")
            }, n = d.createElement(t), n.src = r, n.async = 1, n.onload = n.onreadystatechange = function () {
                var s = this.readyState;
                s && s !== "loaded" && s !== "complete" || (f(), n.onload = n.onreadystatechange = null)
            }, i = d.getElementsByTagName(t)[0], i.parentNode.insertBefore(n, i)
        })(window, document, "script", "//bat.bing.com/bat.js", "uetq");
    </script>

    <script>
        <%-- On the order confirmation page, send revenue value and currency --%>
        if (digitalData.page.pageInfo.pageID === 'orderconfirmationpage') {
            var revenueValue = digitalData.transaction.total.priceWithTax;
            var revenueCurrency = digitalData.transaction.total.cartCurrency;

            window.uetq = window.uetq || [];
            window.uetq.push('event', 'purchase', {
                "revenue_value": revenueValue,
                "currency": revenueCurrency
            });
        }
    </script>
</c:if>
