package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.storefront.validation.annotations.AtLeastOneNotBlank;
import com.namics.distrelec.b2b.storefront.validation.annotations.Phonenumber;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Form object for b2b customer registration
 */
@AtLeastOneNotBlank(message = "{register.onePhone.required}", value = { "phoneNumber", "mobileNumber" })
public class EmployeeRegisterB2BForm {

    private String titleCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String mobileNumber;
    private String faxNumber;
    private String functionCode;
    private String departmentCode;
    private boolean budgetWithoutLimit = true;
    private BigDecimal budgetPerOrder;
    private BigDecimal yearlyBudget;
    private BigDecimal residualBudget;
    private boolean requestQuotationPermission;

    @NotNull(message = "{register.title.invalid}")
    public String getTitleCode() {
        return titleCode;
    }

    public void setTitleCode(final String titleCode) {
        this.titleCode = titleCode;
    }

    @NotBlank(message = "{register.firstName.invalid}")
    @Size(max = 40, message = "{register.firstName.invalid}")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    @NotBlank(message = "{register.lastName.invalid}")
    @Size(max = 40, message = "{register.lastName.invalid}")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    @NotBlank(message = "{register.email.invalid}")
    @Size(max = 175, message = "{register.email.invalid}")
    @Email(message = "{register.email.invalid}")
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @Phonenumber(message = "{register.mobileNumber.invalid}")
    @Size(max = 30, message = "{Size.phoneNumber}")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Phonenumber(message = "{register.phoneNumber.invalid}")
    @Size(max = 30, message = "{Size.mobileNumber}")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Phonenumber(message = "{register.faxNumber.invalid}")
    @Size(max = 30, message = "{Size.faxNumber}")
    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(final String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(final String functionCode) {
        this.functionCode = functionCode;
    }

    public boolean isBudgetWithoutLimit() {
        return budgetWithoutLimit;
    }

    public void setBudgetWithoutLimit(final boolean budgetWithoutLimit) {
        this.budgetWithoutLimit = budgetWithoutLimit;
    }

    public BigDecimal getBudgetPerOrder() {
        return budgetPerOrder;
    }

    public void setBudgetPerOrder(final BigDecimal budgetPerOrder) {
        this.budgetPerOrder = budgetPerOrder;
    }

    public BigDecimal getYearlyBudget() {
        return yearlyBudget;
    }

    public void setYearlyBudget(final BigDecimal yearlyBudget) {
        this.yearlyBudget = yearlyBudget;
    }

    public BigDecimal getResidualBudget() {
        return residualBudget;
    }

    public void setResidualBudget(final BigDecimal residualBudget) {
        this.residualBudget = residualBudget;
    }

    public boolean isRequestQuotationPermission() {
        return requestQuotationPermission;
    }

    public void setRequestQuotationPermission(final boolean requestQuotationPermission) {
        this.requestQuotationPermission = requestQuotationPermission;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }
}
