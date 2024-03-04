package com.yeel.drc.model.beneficiary;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class BeneficiaryDataResponse{

	@SerializedName("non_yeeluser_list")
	private List<NonYeeluserListItem> nonYeeluserList;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setNonYeeluserList(List<NonYeeluserListItem> nonYeeluserList){
		this.nonYeeluserList = nonYeeluserList;
	}

	public List<NonYeeluserListItem> getNonYeeluserList(){
		return nonYeeluserList;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}