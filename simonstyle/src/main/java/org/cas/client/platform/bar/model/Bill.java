package org.cas.client.platform.bar.model;

public class Bill {
	
	private String createTime;
	
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getTableID() {
		return tableID;
	}
	public void setTableID(String tableID) {
		this.tableID = tableID;
	}
	public String getBillIndex() {
		return billIndex;
	}
	public void setBillIndex(String billIndex) {
		this.billIndex = billIndex;
	}
	public float getTotal() {
		return total;
	}
	public void setTotal(float total) {
		this.total = total;
	}
	public float getDiscount() {
		return discount;
	}
	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public float getServiceFee() {
		return serviceFee;
	}
	public void setServiceFee(float serviceFee) {
		this.serviceFee = serviceFee;
	}
	
	public int getCashReceived() {
		return cashReceived;
	}
	public void setCashReceived(int cashReceived) {
		this.cashReceived = cashReceived;
	}
	public int getDebitReceived() {
		return debitReceived;
	}
	public void setDebitReceived(int debitReceived) {
		this.debitReceived = debitReceived;
	}
	public int getVisaReceived() {
		return visaReceived;
	}
	public void setVisaReceived(int visaReceived) {
		this.visaReceived = visaReceived;
	}
	public int getMasterReceived() {
		return masterReceived;
	}
	public void setMasterReceived(int masterReceived) {
		this.masterReceived = masterReceived;
	}
	public int getTip() {
		return tip;
	}
	public void setTip(int tip) {
		this.tip = tip;
	}
	public int getCashback() {
		return cashback;
	}
	public void setCashback(int cashback) {
		this.cashback = cashback;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getComment() {
		return Comment;
	}
	public void setComment(String comment) {
		Comment = comment;
	}
	public String getOpenTime() {
		return OpenTime;
	}
	public void setOpenTime(String openTime) {
		OpenTime = openTime;
	}
	
	public int getOtherReceived() {
		return otherReceived;
	}
	public void setOtherReceived(int otherReceived) {
		this.otherReceived = otherReceived;
	}

	private String tableID;
	private String billIndex;
	private float total = 0f;
	private float discount = 0f;
	private float serviceFee = 0; 
	private int cashReceived = 0;
	private int debitReceived = 0;
	private int visaReceived = 0;
	private int masterReceived = 0; 
	private int otherReceived = 0; 
	private int tip;
	private int cashback;
	private int status;
	private String employeeName;
	private String Comment;
	private String OpenTime;
    
}
