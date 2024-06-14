<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<article id="distcomp-11" class="bannner bannner-component bannner-component--eleven col-md-4">

   
            <div class="bannner__content">

                <img class="bannner__content--logo"  src="${logo.URL}">

                <img class="bannner__content--image" alt="${headline}" src="${image.URL}">

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