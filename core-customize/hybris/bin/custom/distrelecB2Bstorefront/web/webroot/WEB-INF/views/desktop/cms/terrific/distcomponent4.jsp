<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="container">
    <article id="distcomp-4" class="row bannner bannner-component bannner-component--four" >
        <div class="col-md-6 bannner__content">
            <div class="bannner__content--description">
                ${content}
            </div>

        </div>

        <div class="bannner__img-wrapper col-md-6">
            <div class="bannner__img-wrapper--img" style="background-color: ${backgroundColor};background-image: url(${backgroundImage.URL});"></div>
        </div>
    </article>
</div>
