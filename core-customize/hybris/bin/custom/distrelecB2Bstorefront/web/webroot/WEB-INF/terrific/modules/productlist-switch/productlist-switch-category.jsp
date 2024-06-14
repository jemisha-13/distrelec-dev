<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>



<mod:toolsitem template="toolsitem-print" skin="print" tag="div" />
<c:if test="${not empty hideIconViewOption and not hideIconViewOption}">
	<button class="btn btn-secondary btn-switch btn-switch-icon${useIconView ? ' active' : ''}" title="<spring:message code='productlistswitch.iconview' />" >
		<i></i>
	</button>
</c:if>
<button class="btn btn-secondary btn-switch btn-switch-standard${useListView ? ' active' : ''}" title="<spring:message code='productlistswitch.simpleview' />" >
	<i></i>
</button>
<button class="btn btn-secondary btn-switch btn-switch-technical${useTechnicalView ? ' active' : ''}" title="<spring:message code='productlistswitch.technicalview' />" >
	<i></i>
</button>