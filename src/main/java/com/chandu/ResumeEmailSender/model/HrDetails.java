package com.chandu.ResumeEmailSender.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HrDetails {
    private String hrName;
    private String hrEmail;
    private String companyName;
	public String getHrName() {
		return hrName;
	}
	public void setHrName(String hrName) {
		this.hrName = hrName;
	}
	public String getHrEmail() {
		return hrEmail;
	}
	public void setHrEmail(String hrEmail) {
		this.hrEmail = hrEmail;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	@Override
	public String toString() {
		return "HrDetails [hrName=" + hrName + ", hrEmail=" + hrEmail + ", companyName=" + companyName + "]";
	}
	public HrDetails(String hrName, String hrEmail, String companyName) {
		super();
		this.hrName = hrName;
		this.hrEmail = hrEmail;
		this.companyName = companyName;
	}
	public HrDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
    
    
}