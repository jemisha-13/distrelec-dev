<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
​
<div class="col-md-12 welcome-mat-points">
		<div class="row welcome-mat-points__row">
			<div class="col-2 col-md-4 welcome-mat-points__row__icon">
				<img class="img-responsive" alt="${text}" src="${image.URL}">
			</div>
			<div class="col-10 col-md-8 welcome-mat-points__row__text">
				<p>${text}</p>
			</div>
		</div>
</div>
