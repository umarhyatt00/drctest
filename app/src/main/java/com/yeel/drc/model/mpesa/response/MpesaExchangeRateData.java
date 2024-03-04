package com.yeel.drc.model.mpesa.response;

import com.google.gson.annotations.SerializedName;

public class MpesaExchangeRateData {

	@SerializedName("agentCode")
	private String agentCode;

	@SerializedName("payRate")
	private String payRate;

	@SerializedName("remitRate")
	private String remitRate;

	@SerializedName("dated")
	private String dated;

	@SerializedName("currencyCode")
	private String currencyCode;

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public String getPayRate() {
		return payRate;
	}

	public void setPayRate(String payRate) {
		this.payRate = payRate;
	}

	public String getRemitRate() {
		return remitRate;
	}

	public void setRemitRate(String remitRate) {
		this.remitRate = remitRate;
	}

	public String getDated() {
		return dated;
	}

	public void setDated(String dated) {
		this.dated = dated;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
}