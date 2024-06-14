export interface GuestRMACreateRequestForm {
  customerName: string;
  orderNumber: string;
  emailAddress: string;
  phoneNumber: string;
  articleNumber: string;
  quantity: number;
  returnReason: string;
  returnSubReason: string;
  customerText: string;
}

export interface MainReason {
  mainReasonId: string;
  mainReasonText: string;
  defaultSubReasonId: string;
  subReasons: SubReason[];
}

export interface SubReason {
  subReasonId: string;
  subReasonMessages: string[];
}
