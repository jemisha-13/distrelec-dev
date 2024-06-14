<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${!product.catPlusItem}">

{{? it.volumePricesMap.length }}
<div class="table">
{{~ it.volumePricesMap :v:id }}
	{{? id==0 }}
		<div class="table-row">
			<div class="table-cell">
				<span class="label">{{!v.minQuantity}} +</span>
				<span class="value">{{=v.currency}} {{=v.value}}</span>
			</div>
		</div>
	{{?}}
{{~}}
</div>
{{?}}

</c:if>