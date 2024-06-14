<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<ul>
	<c:set var="passed" value="1"/>
	<c:set var="isFirstIcon" value="0" />	
	<c:forEach var="step" items="${processSteps}" varStatus="status" end="2">
		<li data-stepInfoOnly="${step.code}">
			<c:if test="${step.active}">
				<c:set var="passed" value="0"/>
			</c:if>
			<c:set var="clazz" value="passive"/>
			<c:if test="${passed eq 1}">
				<c:set var="clazz" value="passed"/>
			</c:if>
			<c:choose>
				<c:when test="${passed eq 0}">
					<div class="activePassiveStep ${step.active ? 'active' : clazz}"></div>
				</c:when>
				<c:otherwise>
					<a class="${clazz}" href="${step.url}">
				</c:otherwise>
            </c:choose>
	            <i class="back"></i>
                <span class="label">
                    <span class="wrap">
                    	<c:choose>
							<c:when test="${status.index eq 0 and not empty step.stepClass}">
	                    		<i class="${step.stepClass}"></i>
	                    	</c:when>
	                    	<c:otherwise>
	                    		${status.index+1}
	                    	</c:otherwise>                    		
                    	</c:choose>
                    </span>
                </span>
                <span class="title">
                    <span class="wrap"><spring:message code="${step.messageCode}" /></span>
                </span>
          	<c:choose>
				<c:when test="${passed eq 0}">
					</div>
				</c:when>
				<c:otherwise>
					</a>
				</c:otherwise>
            </c:choose>
	    </li>
	    <c:if test="${!status.last}">
	    	<li class="line line-long-import"></li>
	    </c:if>
	    <c:set var="stepCtr" value="${stepCtr + 1}"></c:set>
	</c:forEach>
</ul>
