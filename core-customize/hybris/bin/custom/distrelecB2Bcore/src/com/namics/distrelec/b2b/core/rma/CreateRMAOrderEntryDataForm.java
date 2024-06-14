package com.namics.distrelec.b2b.core.rma;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class CreateRMAOrderEntryDataForm {

	private String itemNumber;
	private String articleNumber;
	private Long quantity;
    private String returnReasonID;
    private String returnSubReason;
    private String refundType = "";
    private String customerText;
    private boolean rmaRaised;

	public String getItemNumber() {
		return itemNumber;
	}
	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	@Min(value = 1)
	public Long getQuantity() {
		return quantity;
	}
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	
	public String getReturnReasonID() {
		return returnReasonID;
	}
	public void setReturnReasonID(String returnReasonID) {
		this.returnReasonID = returnReasonID;
	}
	
	public String getRefundType() {
		return refundType;
	}
	public void setRefundType(String refundType) {
		this.refundType = refundType;
	}
	
	@Length(max=50)
	public String getCustomerText() {
		return customerText;
	}
	public void setCustomerText(String customerText) {
		this.customerText = customerText;
	}
    public String getArticleNumber() {
        return articleNumber;
    }
    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }
    public boolean isRmaRaised() {
        return rmaRaised;
    }
    public void setRmaRaised(boolean rmaRaised) {
        this.rmaRaised = rmaRaised;
    }

	public String getReturnSubReason() {
		return returnSubReason;
	}

	public void setReturnSubReason(String returnSubReason) {
		this.returnSubReason = returnSubReason;
	}
}
