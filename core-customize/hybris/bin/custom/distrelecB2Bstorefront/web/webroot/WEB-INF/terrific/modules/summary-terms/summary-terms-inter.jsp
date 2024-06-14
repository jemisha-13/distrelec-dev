<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<div class="summary__terms">
    <formUtil:ux-formCheckboxPath idKey="summaryCheckbox"
                                  isChecked=""
                                  formGroupClass="ux-form-group"
                                  inputCSS="js-summary-checkbox"
                                  labelKey="newcheckout.acceptTerms"/>
</div>
