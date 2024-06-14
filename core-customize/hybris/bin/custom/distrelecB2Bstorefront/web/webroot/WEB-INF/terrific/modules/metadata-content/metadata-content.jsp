<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>

<spring:theme code="product.list.read.more" text="Read More" var="sShowMore"/>
<spring:theme code="product.list.read.less" text="Read Less" var="sShowLess"/>


<c:if test="${not empty metaData.contentTitle}">
	<div class="metaData-content">
		<c:if test="${not empty metaData.contentTitle && metaData.contentTitle ne ' ' && metaData.contentTitle ne '&nbsp;'}">
    		<h3 class="base page-title">${metaData.contentTitle}</h3>
		</c:if>
		<c:if test="${not empty metaData.contentDescription}">
			<div class="content-text comment more" data-showmore="${sShowMore}" data-showless="${sShowLess}">${metaData.contentDescription}</div> 
		</c:if>	
   </div>  
</c:if>