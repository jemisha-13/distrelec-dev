<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="container">
    <article id="distcomp-15" class="row bannner bannner-component bannner-component--fifteen">
        <div class="col-12 bannner__content" style="background-color: ${backgroundColor};background-image: url(${backgroundImage.URL});">

            <div class="bannner__content--description">
                ${longtext}
            </div>
            <c:if test="${not empty buttonTextURL and not empty buttonText}">
                <a class="bannner__content--cta mat-button__normal--action-blue" href="${buttonTextURL}">
                    ${buttonText} <i class="fas fa-angle-right"></i>
                </a>
            </c:if>

        </div>
    </article>
</div>