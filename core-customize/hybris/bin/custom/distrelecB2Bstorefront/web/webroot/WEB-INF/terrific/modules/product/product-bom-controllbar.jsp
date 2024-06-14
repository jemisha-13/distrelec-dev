<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="formatprice" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>

<div class="bom-product-controllbar">

	<label class="bom-product-controllbar__select-all">
		<input type="checkbox" />
		<span class="mat-checkbox bom-product-controllbar__checkbox"> </span>
		<span class="label"><spring:message code="text.account.orderHistory.orderDetails.returnItems.selectAll"/> </span>
	</label>

	<div class="bom-product-controllbar__onoffswitch" data-aainteraction="change view type">
		<input type="checkbox" name="onoffswitch" class="bom-product-controllbar__onoffswitch-checkbox" id="myonoffswitch" data-aainteraction="change view type">

		<span class="bom-product-controllbar__onoffswitch-text"><spring:message code="text.compact.view"  /></span>
		<label class="bom-product-controllbar__onoffswitch-label" for="myonoffswitch">
			<span class="bom-product-controllbar__onoffswitch-switch"></span>
		</label>
		<span class="bom-product-controllbar__onoffswitch-text"><spring:message code="text.detailed.view"  /></span>
	</div>

</div>