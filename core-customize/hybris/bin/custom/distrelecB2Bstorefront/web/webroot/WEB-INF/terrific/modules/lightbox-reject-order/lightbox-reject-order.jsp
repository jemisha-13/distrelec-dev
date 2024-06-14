<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<spring:theme code="lightboxrejectorder.cancel" var="sCancel" />
<spring:theme code="lightboxrejectorder.submit" var="sSubmit" />

<div class="modal" tabindex="-1">
	<div class="hd">
		<div class="-left">
			<h3 class="title"><spring:theme code="lightboxrejectorder.personalNote" /></h3>
		</div>
		<div class="-right">
			<a class="btn btn-close" href="#" data-dismiss="modal" aria-hidden="true"><spring:theme code="lightboxrejectorder.close" /></a>
		</div>
	</div>
	<form:form id="orderApprovalDecisionForm" modelAttribute="orderApprovalDecisionForm" method="post" action="/my-account/order-approval/approval-decision">
		<input type="hidden" id="rejectWorkFlowActionCode" name="workFlowActionCode" value="${orderApprovalData.workflowActionModelCode}"/>
		<input type="hidden" id="recjectApproverSelectedDecision" name="approverSelectedDecision" value="REJECT"/>
		<input type="hidden" id="rejectTermsAndConditions" name="termsAndConditions" value="true"/>
		<div class="bd">
				<form:textarea id="comments" path="comments"/>
		</div>
		 <div class="ft">
			 <input type="reset" class="btn btn-secondary btn-cancel" value="${sCancel}" />
			 <input type="submit" class="btn btn-primary btn-submit" value="${sSubmit}" />
		</div>
	</form:form>
</div>