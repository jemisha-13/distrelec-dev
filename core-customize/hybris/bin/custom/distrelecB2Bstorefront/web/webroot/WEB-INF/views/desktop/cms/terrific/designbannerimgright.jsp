<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="container">
    <article id="distcomp-1" class="row bannner bannner-component bannner-component--one">
        <div class="col-md-7 bannner__content">
            <div class="bannner__content--description">
                ${longtext}
            </div>
            <c:if test="${not empty buttonTextURL and not empty buttonText}">
                <a class="bannner__content--cta mat-button__normal--action-blue" href="${buttonTextURL}">
                    ${buttonText} <i class="fas fa-angle-right"></i>
                </a>
            </c:if>
           </div>

        <div class="bannner__img-wrapper col-md-5">
            <div class="bannner__img-wrapper--img" style="background-color: ${backgroundColor};background-image: url(${backgroundImage.URL});"></div>
        </div>

    </article>
</div>
