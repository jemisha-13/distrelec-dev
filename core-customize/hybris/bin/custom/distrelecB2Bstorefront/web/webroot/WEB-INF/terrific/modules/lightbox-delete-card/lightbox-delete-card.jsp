<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:theme code="lightboxYesNo.no" text="No" var="cancelButton" />
<spring:theme code="lightboxYesNo.close" text="Close" var="closeButton" />
<spring:theme code="lightboxYesNo.yes" text="Yes" var="confirmButton" />

<div class="modal-backdrop hidden">&nbsp;</div>
<div class="modal base hidden" id="modalDeleteCard" tabindex="-1">
   <div class="modal-content">
       <div class="hd">
           <div class="-left">
               <h3 class="title">

               </h3>
           </div>
       </div>
       <div class="bd">
           <p>

           </p>
       </div>
       <div class="ft">
           <input type="submit" class="mat-button mat-button--action-red btn-cancel" value="${cancelButton}" data-dismiss="modal" aria-hidden="true">
           <input type="submit" class="mat-button mat-button--action-green btn-confirm" value="${confirmButton}" />
       </div>
   </div>
</div>