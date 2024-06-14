<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<article>
  <div id="alphabetNumeric">
    <c:forEach var="manufacturer" items="${manufacturers}" varStatus="status">
		<c:choose>
			<%-- Determine row number --%>
			<c:when test="${status.index == 0}">
				<c:set var="rowNum" scope="page" value="1"/>
				<ul class="row1">
			</c:when>
			<c:when test="${status.index == 28}">
				<c:set var="rowNum" scope="page" value="2"/>
				</ul>
				<ul class="row2">
			</c:when>
			<c:when test="${status.index == 56}">
				<c:set var="rowNum" scope="page" value="3"/>
				</ul>
				<ul class="row3">
			</c:when>
		</c:choose>
		<c:choose>
			<%-- Determine if Alphanumeric cell is clickable --%>
		    <c:when test="${not empty manufacturer.value}">
			    <c:set var="alphaNumType" scope="page" value="activeCell"/>
		 	    <c:set var="disabled" scope="page" value=""/>
		    </c:when>
		    <c:otherwise>
			    <c:set var="alphaNumType" scope="page" value="inactiveCell"/>
			    <c:set var="disabled" scope="page" value="disabled"/>
		    </c:otherwise>
		</c:choose>
			<li class="${alphaNumType}">
				<c:choose>
					<c:when test="${alphaNumType == 'activeCell'}">
					<a href="#alphaNum${manufacturer.key}">
					  <div class="alphaNumLink">
						<span>${manufacturer.key}</span>
					  </div>
					</a>
				  </c:when>
					<c:otherwise>
						<%-- Do not include anchor tag for letter with no entries --%>
						<div class="alphaNumLink">
							<span>${manufacturer.key}</span>
						</div>
				  </c:otherwise>
				</c:choose>
			</li>
  	</c:forEach>
    </ul>
  </div>
</article>

