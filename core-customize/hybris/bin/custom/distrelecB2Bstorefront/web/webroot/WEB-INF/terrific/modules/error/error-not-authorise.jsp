<div class="container error-not-authorise">
    <div class="row">
        <div class="col-sm-12">
            <div id="breadcrumb" class="breadcrumb page__breadcrumb">
                <mod:breadcrumb/>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-sm-12">
            <div class="error-not-authorise__content">
                <h1 class="error-not-authorise__message" >${message}</h1>
                <cms:slot var="feature" contentSlot="${slots['Content']}">
                    <div class="padding-left">
                        <cms:component component="${feature}"/>
                    </div>
                </cms:slot>
            </div>
        </div>
    </div>
</div>