<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<spring:message code='import-tool.file.limitexhausted' arguments="" var="fileUplloadLimitMsg"/>
<spring:message code='import-tool.file.nosavedfiles' arguments="" var="noSavedFiles"/>

<div class="container saved-bom-entries" v-cloak>

    <div class="saved-bom-entries__body">

        <h1 class="base"> <spring:message code="text.savebomuploads" /> </h1>

        <mod:global-messages template="component" skin="component fileuplloadlimit-error hidden"  headline='' body='${fileUplloadLimitMsg}' type="error"/>

        <div class="no-files-saved noSavedFiles hidden">
            ${noSavedFiles}
        </div>

        <div class="saved-bom-entries__item" v-for="(bomFile, index)  in savedBomFileData" v-if="savedBomFileData.length > 0" >
            <input class="saved-bom-entries__item-filename hidden" :value="[[bomFile]]" />
            <div class="saved-bom-entries__filename">{{bomFile}}</div>

            <div class="saved-bom-entries__save-item hidden">
                <input type="text" class="form-control saved-bom-entries__item-filename-ipt" v-model="bomFile" :value="[[bomFile]]" :placeholder="[[bomFile]]"/>
                <button class="mat-button mat-button__solid--action-green saved-bom-entries__save-item-save" v-on:click="fileRenameSave(bomFile)"> <spring:message code="lightboxshopsettings.save" /> </button>
                <button class="mat-button mat-button--action-red saved-bom-entries__save-item-close" v-on:click="fileRenameClose(bomFile)"> <spring:message code="base.close" /> </button>
            </div>

            <div class="wrapper-dropdown right">
                <a class="wrapper-dropdown__top-item" :href="'/bom-tool/load-file?fileName=' + bomFile" > <spring:message code="text.edit" /> </a>
                <button class="wrapper-dropdown__menu" v-on:click="openFileMenu(bomFile)">
                    <i class="fas fa-chevron-down close-icon"></i>
                    <i class="fas fa-chevron-up open-icon"></i>
                </button>
                <ul class="dropdown">
                    <li> <button class="dropbtn saved-bom-entries__item-rename" v-on:click="fileRename(bomFile)"> <spring:message code="text.rename" /> </button> </li>
                    <li> <button class="dropbtn saved-bom-entries__item-duplicate" v-on:click="fileDuplicate(bomFile)"> <spring:message code="text.duplicate" /> </button> </li>
                    <li> <button class="dropbtn saved-bom-entries__item-delete" v-on:click="showFileDeleteModal(bomFile)"> <spring:message code="text.delete" /> </button> </li>
                </ul>
            </div>

        </div>

    </div>

</div>

<!-- Modal -->
<div class="bs-modal modal fade hidden" id="bomEntryDeleteModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="false" style="display: block;">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-body">
                <h2 class="modal__title"> <spring:message code="text.delete.bomfile" /> </h2>
                </br>
            </div>
            <div class="modal-footer">

                <div class="row">

                    <div class="col">
                        <button type="button" class="mat-button mat-button--action-grey col" data-dismiss="modal"> <spring:message code="lightboxYesNo.cancel" />  </button>
                    </div>

                    <div class="col">
                        <button type="button" class="mat-button mat-button--matterhorn mat-button__solid--action-green col btn-bomentrydelete" > <spring:message code="text.button.yesdelete" /> </button>
                    </div>

                </div>
                </br>
                <p class="modal__description"> <spring:message code="text.delete.caution" /> </p>

            </div>
        </div>
    </div>
</div>