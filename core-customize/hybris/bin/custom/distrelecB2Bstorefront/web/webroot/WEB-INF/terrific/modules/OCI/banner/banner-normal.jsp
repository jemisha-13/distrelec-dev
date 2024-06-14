<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>

<c:set var="ctaEnabled" value="false"/>
<spring:message code="topBrands.title" var="sTitle" />

<c:if test="${not empty localizedUrlText}">
    <c:set var="ctaEnabled" value="true"/>
</c:if>

<div class="bannner-normal bannner-normal-OCI" >
        <c:if test="${not ctaEnabled}">

            <c:choose>
                <c:when test="${not empty localizedUrlLink}">
                    <a href="${localizedUrlLink}"
                </c:when>
                <c:otherwise>
                    <span
                </c:otherwise>
            </c:choose>

                <c:forEach var="dataAttributeEntry" items="${dataAttributes}">
                    data-${dataAttributeEntry.key}="${dataAttributeEntry.value}"
                </c:forEach>
                    class="bannner-normal__link">
        </c:if>

        <img src="${media.url}" width="178" height="57" title="${sTitle}" class="bannner-normal__img" alt="${headline}"/>

		<div class="bannner-normal__content">        
        	<c:if test="${not empty headline}">
              <h1 class="bannner-normal__heading">${headline}</h1>
        	</c:if>
        	<c:if test="${not empty content}">
               <p class="bannner-normal__description">${content}</p>
         	</c:if>  
        	<c:if test="${ctaEnabled}">
            	<a class="bannner-normal__cta mat-button mat-button__solid--action-blue" href="${localizedUrlLink}"
                  <c:forEach var="dataAttributeEntry" items="${dataAttributes}">
                        data-${dataAttributeEntry.key}="${dataAttributeEntry.value}"
                   </c:forEach>
             	>${localizedUrlText}</a>
        	</c:if>        
         </div> 
            
        <c:if test="${not ctaEnabled}">

            <c:choose>
                <c:when test="${not empty localizedUrlLink}">
                    </a>
                </c:when>
                <c:otherwise>
                    </span>
                </c:otherwise>
            </c:choose>

        </c:if>

</div>
