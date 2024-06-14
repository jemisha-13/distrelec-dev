<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="modal" id="modalStatus" tabindex="-1">
    <div class="hd">
        <div class="-left"> <h3 class="title"></h3> </div>
        <div class="-right"> <a class="btn btn-close" href="#" data-dismiss="modal" aria-hidden="true"><spring:message code="lightboxshoppinglist.close" /></a> </div>
    </div>
    <div class="bd base">
        <div class="box"> <h4>Error</h4> <p></p> </div>
    </div>
    <div class="ft"> <input type="submit" class="btn btn-primary btn-confirm" value="<spring:message code='lightboxstatus.okay' />" /> </div>
</div>